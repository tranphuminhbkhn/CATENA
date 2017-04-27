package catena.embedding.experiments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import catena.Temporal;
import catena.evaluator.PairEvaluator;
import catena.parser.entities.TLINK;

public class EvaluateTimeBankDense {

	public static void main(String[] args) throws Exception {	
		
		boolean colFilesAvailable = true;
		boolean train = true;
		TimeBankDense(colFilesAvailable, train);
		
	}
	
	public static void TimeBankDense(boolean colFilesAvailable, boolean train) throws Exception {
		String[] devDocs = { 
			"APW19980227.0487.tml", 
			"CNN19980223.1130.0960.tml", 
			"NYT19980212.0019.tml",  
			"PRI19980216.2000.0170.tml", 
			"ed980111.1130.0089.tml" 
		};
			
		String[] testDocs = { 
			"APW19980227.0489.tml",
			"APW19980227.0494.tml",
			"APW19980308.0201.tml",
			"APW19980418.0210.tml",
			"CNN19980126.1600.1104.tml",
			"CNN19980213.2130.0155.tml",
			"NYT19980402.0453.tml",
			"PRI19980115.2000.0186.tml",
			"PRI19980306.2000.1675.tml" 
		};
		
		String[] trainDocs = {
			"APW19980219.0476.tml",
			"ea980120.1830.0071.tml",
			"PRI19980205.2000.1998.tml",
			"ABC19980108.1830.0711.tml",
			"AP900815-0044.tml",
			"CNN19980227.2130.0067.tml",
			"NYT19980206.0460.tml",
			"APW19980213.1310.tml",
			"AP900816-0139.tml",
			"APW19980227.0476.tml",
			"PRI19980205.2000.1890.tml",
			"CNN19980222.1130.0084.tml",
			"APW19980227.0468.tml",
			"PRI19980213.2000.0313.tml",
			"ABC19980120.1830.0957.tml",
			"ABC19980304.1830.1636.tml",
			"APW19980213.1320.tml",
			"PRI19980121.2000.2591.tml",
			"ABC19980114.1830.0611.tml",
			"APW19980213.1380.tml",
			"ea980120.1830.0456.tml",
			"NYT19980206.0466.tml"
		};
		
		Map<String, Map<String, String>> tlinkPerFile = Temporal.getTimeBankDenseTlinks("./data/TimebankDense.T3.txt");
		
		Temporal temp;
		PairEvaluator ptt, ped, pet, pee;
		Map<String, String> relTypeMapping;
		List<TLINK> tlinks;
		
		// TimeBank-Dense
		String[] tbDenseLabel = {"BEFORE", "AFTER", "SIMULTANEOUS", 
				"INCLUDES", "IS_INCLUDED", "VAGUE"};
		String taskName = "tbdense";
		
		temp = new Temporal(true, tbDenseLabel,
				"./models/" + taskName + "-event-dct.model",
				"./models/" + taskName + "-event-timex.model",
				"./models/" + taskName + "-event-event.model",
				false, true, false,
				false, false);
		
		// TRAIN
		if (train) {
			temp.trainModels(taskName, "./data/TempEval3-train_TML/", trainDocs, tlinkPerFile, tbDenseLabel, colFilesAvailable);
		}
		
		// PREDICT
		relTypeMapping = new HashMap<String, String>();
		relTypeMapping.put("IDENTITY", "SIMULTANEOUS");
		relTypeMapping.put("BEGINS", "BEFORE");
		relTypeMapping.put("BEGUN_BY", "AFTER");
		relTypeMapping.put("ENDS", "AFTER");
		relTypeMapping.put("ENDED_BY", "BEFORE");
		relTypeMapping.put("DURING", "SIMULTANEOUS");
		relTypeMapping.put("DURING_INV", "SIMULTANEOUS");
		tlinks = temp.extractRelations(taskName, "./data/TempEval3-train_TML/", testDocs, tlinkPerFile, tbDenseLabel, relTypeMapping, colFilesAvailable);
		
		// EVALUATE
		System.out.println("********** EVALUATION RESULTS **********");
		System.out.println();
		System.out.println("********** TLINK TIMEX-TIMEX ***********");
		ptt = new PairEvaluator(tlinks.get(1).getTT());
		ptt.evaluatePerLabel(tbDenseLabel);
		System.out.println();
		System.out.println("*********** TLINK EVENT-DCT ************");
		ped = new PairEvaluator(tlinks.get(1).getED());
		ped.evaluatePerLabel(tbDenseLabel);
		System.out.println();
		System.out.println("********** TLINK EVENT-TIMEX ***********");
		pet = new PairEvaluator(tlinks.get(1).getET());
		pet.evaluatePerLabel(tbDenseLabel);
		System.out.println();
		System.out.println("********** TLINK EVENT-EVENT ***********");
		pee = new PairEvaluator(tlinks.get(1).getEE());
		pee.evaluatePerLabel(tbDenseLabel);
	}
	
}
