package classif.fastkmeans;

import items.ClassedSequence;
import items.Sequence;

import java.util.ArrayList;
import java.util.HashMap;

import classif.Prototyper;
import weka.core.Instances;

public class DTWKNNClassifierKMeansCached extends Prototyper {

	private static final long serialVersionUID = 1717176683182910935L;
	HashMap<String, double[][]>distancesPerClass=null;
	
	public DTWKNNClassifierKMeansCached() {
		super();
	}
	
	protected void initDistances() {
		distancesPerClass = new HashMap<>();
		ArrayList<String> classes = new ArrayList<String>(classedData.keySet());
		
		for (String clas : classes) {
			// if the class is empty, continue
			ArrayList<Sequence> objectsInClass = classedData.get(clas);
			if(objectsInClass.isEmpty()) continue;
			int nObjectsInClass = objectsInClass.size();
			
			double[][]distances = new double[nObjectsInClass][nObjectsInClass];
			for(int i=0;i<nObjectsInClass;i++){
				for(int j=i+1;j<nObjectsInClass;j++){
					distances[i][j]=objectsInClass.get(i).distance(objectsInClass.get(j));
					distances[j][i]=distances[i][j];
				}
			}
			distancesPerClass.put(clas, distances);
		}
//		System.out.println("all distances cached");
	}

	@Override
	protected void buildSpecificClassifier(Instances data) {
		if(distancesPerClass==null){
			initDistances();
		}
		
		ArrayList<String> classes = new ArrayList<String>(classedData.keySet());
		
		for (String clas : classes) {
			// if the class is empty, continue
			if(classedData.get(clas).isEmpty()) 
				continue;
			KMeansCachedSymbolicSequence kmeans = new KMeansCachedSymbolicSequence(nbPrototypesPerClass, classedData.get(clas),distancesPerClass.get(clas));
			kmeans.cluster();
			
			for (int i = 0; i < kmeans.centers.length; i++) {
				if(kmeans.centers[i]!=null){ //~ if empty cluster
					ClassedSequence s = new ClassedSequence(kmeans.centers[i], clas);
					prototypes.add(s);
				}
			}
		}
	}
}
