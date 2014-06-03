# Install

## Install custom gephi-toolkit to maven

Build the custom gephi-toolkit
	
    mvn clean install

Or install the jar to the local maven repository 

	cd <project_path>/lib
	mvn install:install-file -Dfile=gephi-toolkit-0.8.2-all.jar -DgroupId=org.gephi -DartifactId=gephi-toolkit -Dversion=0.8.2 -Dpackaging=jar

## Build

	cd <project_path>
	mvn clean package

The resulting jar with dependencies is : 

	<project_path>/target/gephi-toolkit-metrics-0.0.1-jar-with-dependencies.jar


# Customized

Edit this Java Class :

	<project_path>/src/fr/sciencespo/gephi/toolkit/metrics/LayoutAndMetrics.java

# Usage

## GEXF files in the data_input directory

Copy gexf files in the data_input directory.

## Launch

    cd <project_path>
    java -jar -Xms256m -Xmx1500m target/gephi-toolkit-metrics-0.0.1-jar-with-dependencies.jar fr.sciencespo.gephi.toolkit.metrics.Main data_input data_output

## Output example

    input_file: /Users/jrault/Documents/SciencesPo/Projets/Gephi/workspace/gephi-toolkit-metrics/data_input/arxiv_condensed_matter.gexf
    #### Starting processing: arxiv_condensed_matter.gexf
    # Nodes loaded: 23133
    # Edges loaded: 186936
    #benchmark,FA2_LL,0,1.9909512266783038
    #benchmark,FA2_LL,1,1.9896039318942114
    #benchmark,FA2_LL,2,1.9900893310036585
    #benchmark,FA2_LL,3,1.9900043141319725
    #benchmark,FA2_LL,4,1.9896762400471801
    #benchmark,FA2_LL,7,1.9910804337194385
    #benchmark,FA2_LL,8,1.9908752245339356
    #benchmark,FA2_LL,15,1.9941195448811844
    #benchmark,FA2_LL,16,1.9949428188629248
    #benchmark,FA2_LL,31,1.9973011139474273
    #benchmark,FA2_LL,32,1.9974267247518303
    #benchmark,FA2_LL,63,2.005117426996493
    #benchmark,FA2_LL,64,2.00609378790309
    #benchmark,FA2_LL,127,2.019807575377243
    #benchmark,FA2_LL,128,2.019881534175861
    #benchmark,FA2_LL,255,2.0152418478385696
    #benchmark,FA2_LL,256,2.0151484511649325
    #benchmark,FA2_LL,511,2.0006951496660665
    #benchmark,FA2_LL,512,2.0006564120317853
    #benchmark,FA2_LL,1023,1.993533748476143
    #benchmark,FA2_LL,1024,1.993525344796471
    #### Ending processing: arxiv_condensed_matter.gexf in 758238 milliseconds
    input_file: /Users/jrault/Documents/SciencesPo/Projets/Gephi/workspace/gephi-toolkit-metrics/data_input/arxiv_general_relativity.gexf
    #### Starting processing: arxiv_general_relativity.gexf
    # Nodes loaded: 5242
    # Edges loaded: 28980
    #benchmark,FA2_LL,0,1.9984934750675776
    #benchmark,FA2_LL,1,1.994474684196317
    #benchmark,FA2_LL,2,1.9960996492646332
    #benchmark,FA2_LL,3,1.9976794994212441
    #benchmark,FA2_LL,4,1.9977934918975544
    #benchmark,FA2_LL,7,1.9987313084670633
    #benchmark,FA2_LL,8,2.001363817326793
    #benchmark,FA2_LL,15,2.00453197284902
    #benchmark,FA2_LL,16,2.006003887878264
    #benchmark,FA2_LL,31,2.0230283973884724
    #benchmark,FA2_LL,32,2.024442730603705
    #benchmark,FA2_LL,63,2.039332398001594
    #benchmark,FA2_LL,64,2.0395494763395523
    #benchmark,FA2_LL,127,2.0660298406034268
    #benchmark,FA2_LL,128,2.0669248424943416
    #benchmark,FA2_LL,255,2.2416823812681415
    #benchmark,FA2_LL,256,2.2423455107987604
    #benchmark,FA2_LL,511,2.2403250500936696
    #benchmark,FA2_LL,512,2.2405608414044655
    #benchmark,FA2_LL,1023,2.1119982045679935
    #benchmark,FA2_LL,1024,2.11146538120077
    #### Ending processing: arxiv_general_relativity.gexf in 81071 milliseconds
    input_file: /Users/jrault/Documents/SciencesPo/Projets/Gephi/workspace/gephi-toolkit-metrics/data_input/arxiv_high_energy_physics.gexf
    #### Starting processing: arxiv_high_energy_physics.gexf
    # Nodes loaded: 12008
    # Edges loaded: 237010
    #benchmark,FA2_LL,0,2.0079620385946044
    #benchmark,FA2_LL,1,2.0086964899346644
    #benchmark,FA2_LL,2,2.0085415300018585
    #benchmark,FA2_LL,3,2.0090306283369483
    #benchmark,FA2_LL,4,2.009708982968125
    #benchmark,FA2_LL,7,2.0143450369140146
    #benchmark,FA2_LL,8,2.015946993223891
    #benchmark,FA2_LL,15,2.0383732421263487
    #benchmark,FA2_LL,16,2.0411525656596408
    #benchmark,FA2_LL,31,2.0828851925155893
    #benchmark,FA2_LL,32,2.08456132637695
    #benchmark,FA2_LL,63,2.1117072143888707
    #benchmark,FA2_LL,64,2.1112224132141884
    #benchmark,FA2_LL,127,2.0997414501136875
    #benchmark,FA2_LL,128,2.099405620825135
    #benchmark,FA2_LL,255,2.0535987815097334
    #benchmark,FA2_LL,256,2.0533836910028493
    #benchmark,FA2_LL,511,2.035707636871968
    #benchmark,FA2_LL,512,2.035761751287403
    #benchmark,FA2_LL,1023,2.0711861661226862
    #benchmark,FA2_LL,1024,2.07123617567153
    #### Ending processing: arxiv_high_energy_physics.gexf in 354677 milliseconds
