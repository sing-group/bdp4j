/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdp4j.ml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.sf.javaml.classification.Classifier;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import org.bdp4j.types.CSVDataset;

/**
 *
 * @author María Novo
 */
public class CSVDatasetUtils {

    private int K_FOLD_CROSS_VAL = 10;
    private int SPAM_PERCENTAGE = 50;
    private int HAM_PERCENTAGE = 50;
    private int k = K_FOLD_CROSS_VAL;
    public int spamPercentage = SPAM_PERCENTAGE;
    public int hamPercentage = HAM_PERCENTAGE;

    public CSVDatasetUtils() {
    }

    public CSVDatasetUtils(int k) {
        this.k = k;
    }

    public ArrayList<Integer> getConfusionMatrix(CSVDataset dataset, Classifier knn) {
        int fp = 0;
        int fn = 0;
        int tp = 0;
        int tn = 0;

        for (Instance inst : dataset) {
            Object predictedClassValue = knn.classify(inst);
            Object realClassValue = inst.classValue();
            if (predictedClassValue.equals(realClassValue) && predictedClassValue.equals("1")) {// True positive
                tp++;
            } else if (predictedClassValue.equals(realClassValue) && predictedClassValue.equals("0")) {// True negative
                tn++;
            } else if (!predictedClassValue.equals(realClassValue) && predictedClassValue.equals("1")) { // False positive
                fp++;
            } else if (!predictedClassValue.equals(realClassValue) && predictedClassValue.equals("0")) {// False negative
                fn++;
            }
        }
        ArrayList<Integer> confusionMatrix = new ArrayList<>();
        confusionMatrix.add(0, fp);
        confusionMatrix.add(0, fn);
        confusionMatrix.add(0, tp);
        confusionMatrix.add(0, tn);

        return confusionMatrix;
    }

    public CSVDataset sort(CSVDataset dataset) {
        List<String> attributes = dataset.getAttributes();
        List<Instance> spamList = new ArrayList<>();
        List<Instance> hamList = new ArrayList<>();
        int lastInstanceElement = attributes.size() - 1;
        CSVDataset sortedDataset = new CSVDataset(attributes);

        for (Instance instance : dataset) {
            // Se considera que spam -> target=1 ¿Deberíamos poder parametrizar esto?
            if (instance.value(lastInstanceElement) == 1) {
                spamList.add(instance);
            } else {
                hamList.add(instance);
            }
        }
        sortedDataset.addAll(spamList);
        sortedDataset.addAll(hamList);

        return sortedDataset;
    }

    public CSVDataset kFoldCrossValidation(CSVDataset dataset) {
        if (this.k == 0) {
        }

        return new CSVDataset(new ArrayList<>());
    }

    public CSVDataset kFoldCrossValidation(CSVDataset dataset, int k) {
        this.k = k;
        return kFoldCrossValidation(dataset);
    }

    public CSVDataset kFoldCrossValidation(CSVDataset dataset, int spamPercentage, int hamPercentage) {
        this.spamPercentage = spamPercentage;
        this.hamPercentage = hamPercentage;
        return kFoldCrossValidation(dataset);
    }

    public CSVDataset kFoldCrossValidation(CSVDataset dataset, int k, int spamPercentage, int hamPercentage) {
        this.spamPercentage = spamPercentage;
        this.hamPercentage = hamPercentage;
        return kFoldCrossValidation(dataset, k);
    }

    // kFoldCrossValitation donde k = nº instancias
    public CSVDataset leaveOneOut(CSVDataset dataset) {
        int numOfInstances = dataset.size();
        return kFoldCrossValidation(dataset, numOfInstances);
    }

    public CSVDataset leaveOneOut(CSVDataset dataset, int spamPercentage, int hamPercentage) {
        int numOfInstances = dataset.size();
        return kFoldCrossValidation(dataset, numOfInstances, spamPercentage, hamPercentage);
    }

    public CSVDataset datasetConcat(CSVDataset firstDataset, CSVDataset secondDataset) {
        return new CSVDataset(new ArrayList<>());
    }

    public CSVDataset datasetSplit(CSVDataset dataset) {
        return new CSVDataset(new ArrayList<>());
    }

    public CSVDataset datasetJoin(CSVDataset firstDataset, CSVDataset secondDataset) {
        return new CSVDataset(new ArrayList<>());
    }

}
