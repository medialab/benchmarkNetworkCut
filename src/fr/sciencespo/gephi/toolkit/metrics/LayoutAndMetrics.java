package fr.sciencespo.gephi.toolkit.metrics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.gephi.graph.api.Edge;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.HierarchicalGraph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeData;
import org.gephi.io.exporter.api.ExportController;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.force.StepDisplacement;
import org.gephi.layout.plugin.force.yifanHu.YifanHu;
import org.gephi.layout.plugin.force.yifanHu.YifanHuLayout;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingold;
import org.gephi.layout.plugin.fruchterman.FruchtermanReingoldBuilder;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

public class LayoutAndMetrics {

    public void benchmarkFA2(File input_file, File output_dir) {
        benchmark(input_file, output_dir, "FA2", new ProcessFA2());
    }
    
    public void benchmarkFA2_LL(File input_file, File output_dir) {
        benchmark(input_file, output_dir, "FA2_LL", new ProcessFA2_LL());
    }
    
    public void benchmarkFR(File input_file, File output_dir) {
        benchmark(input_file, output_dir, "FR", new ProcessFR());
    }
    
    public void benchmarkYH(File input_file, File output_dir) {
        benchmark(input_file, output_dir, "YH", new ProcessYH());
    }
    
    private void benchmark(File input_file, File output_dir, String algoSignature, Process process){
        System.out.println("#### Starting processing " + algoSignature + " - " + input_file.getName());
        long startTime = System.currentTimeMillis();
		
        // Init a project - and therefore a workspace
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
        Workspace workspace = pc.getCurrentWorkspace();

        // Import the gexf file
        ImportController importController = Lookup.getDefault().lookup(ImportController.class);
        Container container;
        try {
            container = importController.importFile(input_file);
            container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
            //container.getLoader().setEdgeDefault(EdgeDefault.UNDIRECTED);   //Force UNDIRECTED
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        // Append imported data to GraphAPI
        importController.process(container, new DefaultProcessor(), workspace);

        // Init the graphModel
        GraphModel graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();

        String csvText = process.process(graphModel, algoSignature);
        
        // Export the spatialized graph
        ExportController ec = Lookup.getDefault().lookup(ExportController.class);
        try {
        	//String output_file_path = output_dir + File.separator + input_file.getName();
        	String output_file_path = output_dir + File.separator + algoSignature + "_" + input_file.getName();
        	File output_file = new File(output_file_path);
            ec.exportFile(output_file);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        
        // Export the result CSV
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(output_dir + File.separator + algoSignature + "_" + input_file.getName() + ".csv"));
            br.write(csvText);
            br.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("#### Ending processing " + algoSignature + " - " + input_file.getName() + " in " + (endTime - startTime) + " milliseconds");
        
        pc.closeCurrentWorkspace();
        pc.closeCurrentProject();
    }
    
    private abstract class Process{
        public String process(GraphModel graphModel, String algoSignature){
            String report = "there,was,an,error";
            return report;
        }
    }
    
    private class ProcessFA2 extends Process{
        @Override
        public String process(GraphModel graphModel, String algoSignature){
            String report = "";
            // Layout with ForceAtlas2
            ForceAtlas2 forceAtlas2 = new ForceAtlas2(new ForceAtlas2Builder());
            forceAtlas2.setGraphModel(graphModel);

            forceAtlas2.initAlgo();
            forceAtlas2.resetPropertiesValues();
            
            forceAtlas2.setJitterTolerance(1.);
            forceAtlas2.setBarnesHutOptimize(true);
            forceAtlas2.setScalingRatio(2.0);
            forceAtlas2.setGravity(0.);
            
            
            // Log settings
            System.out.println("BarnesHutTheta " + forceAtlas2.getBarnesHutTheta());
            System.out.println("EdgeWeightInfluence " + forceAtlas2.getEdgeWeightInfluence());
            System.out.println("Gravity " + forceAtlas2.getGravity());
            System.out.println("JitterTolerance " + forceAtlas2.getJitterTolerance());
            System.out.println("ScalingRatio " + forceAtlas2.getScalingRatio());
            System.out.println("AdjustSizes " + forceAtlas2.isAdjustSizes());
            System.out.println("BarnesHutOptimize " + forceAtlas2.isBarnesHutOptimize());
            System.out.println("LinLogMode " + forceAtlas2.isLinLogMode());
            System.out.println("OutboundAttractionDistribution " + forceAtlas2.isOutboundAttractionDistribution());
            System.out.println("StrongGravityMode " + forceAtlas2.isStrongGravityMode());

            report = report + "\n" + buildReportRow(graphModel, algoSignature, 0);
            for (int i = 0; i < 2048 && forceAtlas2.canAlgo(); i++) {
                  forceAtlas2.goAlgo();
                  if(isPowerOfTwo(i+1) || isPowerOfTwo(i+2)){
                      report = report + "\n" + buildReportRow(graphModel, algoSignature, i+1);
                  }
                  if(i%100 == 99){
                      System.out.println("Step " + (i+1) + " for " + algoSignature);
                  }
            }
            forceAtlas2.endAlgo();
            
            return report;
        }
    }
    
    private class ProcessFA2_LL extends Process{
        @Override
        public String process(GraphModel graphModel, String algoSignature){
            String report = "";
            // Layout with ForceAtlas2
            ForceAtlas2 forceAtlas2 = new ForceAtlas2(new ForceAtlas2Builder());
            forceAtlas2.setGraphModel(graphModel);

            forceAtlas2.initAlgo();
            forceAtlas2.resetPropertiesValues();
            
            forceAtlas2.setLinLogMode(Boolean.TRUE);

            forceAtlas2.setJitterTolerance(1.);
            forceAtlas2.setBarnesHutOptimize(true);
            forceAtlas2.setScalingRatio(2.0);
            forceAtlas2.setGravity(0.);

            // Log settings
            System.out.println("BarnesHutTheta " + forceAtlas2.getBarnesHutTheta());
            System.out.println("EdgeWeightInfluence " + forceAtlas2.getEdgeWeightInfluence());
            System.out.println("Gravity " + forceAtlas2.getGravity());
            System.out.println("JitterTolerance " + forceAtlas2.getJitterTolerance());
            System.out.println("ScalingRatio " + forceAtlas2.getScalingRatio());
            System.out.println("AdjustSizes " + forceAtlas2.isAdjustSizes());
            System.out.println("BarnesHutOptimize " + forceAtlas2.isBarnesHutOptimize());
            System.out.println("LinLogMode " + forceAtlas2.isLinLogMode());
            System.out.println("OutboundAttractionDistribution " + forceAtlas2.isOutboundAttractionDistribution());
            System.out.println("StrongGravityMode " + forceAtlas2.isStrongGravityMode());
            
            report = report + "\n" + buildReportRow(graphModel, algoSignature, 0);
            for (int i = 0; i < 2048 && forceAtlas2.canAlgo(); i++) {
                  forceAtlas2.goAlgo();
                  if(isPowerOfTwo(i+1) || isPowerOfTwo(i+2)){
                      report = report + "\n" + buildReportRow(graphModel, algoSignature, i+1);
                  }
                  if(i%100 == 99){
                      System.out.println("Step " + (i+1) + " for " + algoSignature);
                  }
            }
            forceAtlas2.endAlgo();
            
            return report;
        }
    }
    
    private class ProcessFR extends Process{
        @Override
        public String process(GraphModel graphModel, String algoSignature){
            String report = "";
            // Layout with Fruchterman Reingold
            FruchtermanReingold fr = new FruchtermanReingold(new FruchtermanReingoldBuilder());
            
            fr.setGraphModel(graphModel);
            fr.initAlgo();
            fr.resetPropertiesValues();
            
            // Log settings
            System.out.println("Area " + fr.getArea());
            System.out.println("Gravity " + fr.getGravity());
            System.out.println("Speed " + fr.getSpeed());
            
            report = report + "\n" + buildReportRow(graphModel, algoSignature, 0);
            for (int i = 0; i < 2048 && fr.canAlgo(); i++) {
                  fr.goAlgo();
                  if(isPowerOfTwo(i+1) || isPowerOfTwo(i+2)){
                      report = report + "\n" + buildReportRow(graphModel, algoSignature, i+1);
                  }
                  if(i%100 == 99){
                      System.out.println("Step " + (i+1) + " for " + algoSignature);
                  }
            }
            fr.endAlgo();
            
            return report;
        }
    }
    
    private class ProcessYH extends Process{
        @Override
        public String process(GraphModel graphModel, String algoSignature){
            String report = "";
            // Layout with Yifan Hu
            YifanHuLayout yh = new YifanHuLayout(new YifanHu(), new StepDisplacement(1f));

            yh.setGraphModel(graphModel);
            yh.initAlgo();
            yh.resetPropertiesValues();
            
            // Log settings
            System.out.println("BarnesHutTheta " + yh.getBarnesHutTheta());
            System.out.println("ConvergenceThreshold " + yh.getConvergenceThreshold());
            System.out.println("InitialStep " + yh.getInitialStep());
            System.out.println("OptimalDistance " + yh.getOptimalDistance());
            System.out.println("QuadTreeMaxLevel " + yh.getQuadTreeMaxLevel());
            System.out.println("RelativeStrength " + yh.getRelativeStrength());
            System.out.println("StepRatio " + yh.getStepRatio());
            System.out.println("AdaptiveCooling " + yh.isAdaptiveCooling());
            
            report = report + "\n" + buildReportRow(graphModel, algoSignature, 0);
            for (int i = 0; i < 2048 && yh.canAlgo(); i++) {
                  yh.goAlgo();
                  if(isPowerOfTwo(i+1) || isPowerOfTwo(i+2)){
                      report = report + "\n" + buildReportRow(graphModel, algoSignature, i+1);
                  }
                  if(i%100 == 99){
                      System.out.println("Step " + (i+1) + " for " + algoSignature);
                  }
            }
            yh.endAlgo();
            
            return report;
        }
    }
    
    private String buildReportRow(GraphModel graphModel, String algoSignature, int step){
        long time = System.currentTimeMillis();
        String line = String.valueOf(step) + "," + String.valueOf(computeNeal(graphModel.getHierarchicalGraph()) + "," + time);
        System.out.println("#benchmark\t" + line);
        return line;
    }
    
    
    
    
    static boolean isPowerOfTwo(int n) {     
        return ((n != 0) && (n & (n-1)) == 0);
    }
    
    public double computeNeal(HierarchicalGraph graph){
        Node[] nodes = graph.getNodes().toArray();
        Edge[] edges = graph.getEdgesAndMetaEdges().toArray();

        
        // We compute Noack's normalized^endv atedge length
        double card_e = graph.getEdgeCount();
        double card_n2 = graph.getNodeCount() * graph.getNodeCount();
        double sum_edges_distances = 0;
        for(Edge e : edges){
            NodeData sourceData = e.getSource().getNodeData();
            NodeData targetData = e.getTarget().getNodeData();
            double distance = Math.sqrt((sourceData.x() - targetData.x())*(sourceData.x() - targetData.x()) + (sourceData.y() - targetData.y())*(sourceData.y() - targetData.y()));
            sum_edges_distances += distance;
        }
        double sum_npairs_distances = 0;
        for (Node n1 : nodes) {
            NodeData nData1 = n1.getNodeData();
            for (Node n2 : nodes) {
                NodeData nData2 = n2.getNodeData();
                if(n1.getId() < n2.getId()){
                    double distance = Math.sqrt((nData1.x() - nData2.x())*(nData1.x() - nData2.x()) + (nData1.y() - nData2.y())*(nData1.y() - nData2.y()));
                    sum_npairs_distances += distance;
                }
            }
        }
        double neal = (sum_edges_distances / card_e) / (sum_npairs_distances / card_n2);
        
        return neal;
    }
    
    public void computeEdgeCrossings(){
        /*
        // We compute the number of edge crossings
        // http://www.dcs.gla.ac.uk/publications/PAPERS/6621/final.pdf
        double c_all = (card_e * (card_e - 1)) / 2;
        double c_impossible = 0;
        for (Node n : nodes) {
            double degree = graph.getDegree(n);
            c_impossible += degree * (degree - 1);
        }
        c_impossible = c_impossible/2;
        double c_max = c_all - c_impossible;
        double aleph_c;
        if(c_max > 0){
            double c = 0;
            for(Edge e1 : edges){
                NodeData sourceData1 = e1.getSource().getNodeData();
                NodeData targetData1 = e1.getTarget().getNodeData();
                for(Edge e2 : edges){
                    if(e1.getId() < e2.getId()){
                        NodeData sourceData2 = e2.getSource().getNodeData();
                        NodeData targetData2 = e2.getTarget().getNodeData();
                        if(doLineSegmentsIntersect(sourceData1.x(), sourceData1.y(), targetData1.x(), targetData1.y(), sourceData2.x(), sourceData2.y(), targetData2.x(), targetData2.y())){
                            c += 1;
                        }
                    }
                }
            }
            aleph_c = 1 - c / c_max;
        } else {
            aleph_c = 0;
        }

        */
    }
    
    public boolean doLineSegmentsIntersect(double px, double py, double p2x, double p2y, double qx, double qy, double q2x, double q2y){
        double rx = p2x - px;
        double ry = p2y - py;
        double sx = q2x - qx;
        double sy = q2y - qy;
        
        if(((px == qx) && (py == qy)) || ((px == q2x) && (py == q2y)) || ((p2x == qx) && (p2y == qy)) || ((p2x == q2x) && (p2y == q2y))){
            return false;
        }
        
        double sub_qp_x = qx - px;
        double sub_qp_y = qy - py;
        
        double uNumerator = sub_qp_x * ry - sub_qp_y * rx;
        double denominator = rx * sy - ry * sx;
        
        if(uNumerator == 0 && denominator == 0){
            // Colinear, so do they overlap ?
            return ((qx - px < 0) != (qx - p2x < 0) != (q2x - px < 0) != (q2x - p2x < 0)) || ((qy - py < 0) != (qy - p2y < 0) != (q2y - py < 0) != (q2y - p2y < 0));
        }
        
        if(denominator == 0){
            // Parallel
            return false;
        }
        
        double u = uNumerator / denominator;
        double t = (sub_qp_x * sy - sub_qp_y * sx) / denominator;
        
        return (t >= 0) && (t <= 1) && (u >= 0) && (u <= 1);
    }
}
