package org.group.sensim.eval;

import edu.stanford.nlp.parser.metrics.Eval;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * This class serves as organizing and saving the results from the evaluation.
 */
public class EvaluationDataManager {
    /* True positive variable */
    private int tp;
    /* False positive variable */
    private int fp;
    /* False negative variable */
    private int fn;

    public EvaluationDataManager(){
        this.tp = 0;
        this.fp = 0;
        this.fn = 0;
    }
    /* Scenario TP: basisDocument --> NER, FOX ---> NER */
    /* Scenario FP: basisDocument --> no NER, FOX ---> NER */
    /* Scenario FN: basisDocument --> NER, FOX ---> no NER */
    /* Scenario TN not interesting for calculating precision, recall, f-score. */

    /**
     * Increases true-positive counter by 1.
     */
    public void incrementTP(){
        this.tp++;
    }

    /**
     * Increases false-positive counter by 1.
     */
    public void incrementFP(){
        this.fp++;
    }

    /**
     * Increases false-negative counter by 1.
     */
    public void incrementFN(){
        this.fn++;
    }

    private BigDecimal calculatePrecision(){
        BigDecimal tpBig = BigDecimal.valueOf(this.tp);
        BigDecimal fpBig = BigDecimal.valueOf(this.fp);

        BigDecimal sumTpFp = tpBig.add(fpBig);
        BigDecimal precision = tpBig.divide(sumTpFp, new MathContext(4, RoundingMode.HALF_UP));
        return precision;
    }

    private BigDecimal calculateRecall() {
        BigDecimal tpBig = BigDecimal.valueOf(this.tp);
        BigDecimal fnBig = BigDecimal.valueOf(this.fn);
        BigDecimal sumTpFn = tpBig.add(fnBig);
        BigDecimal recall = tpBig.divide(sumTpFn, new MathContext(4, RoundingMode.HALF_UP));

        return recall;
    }

    private BigDecimal calculateF1Score() {
        BigDecimal p = calculatePrecision();
        BigDecimal r = calculateRecall();
        BigDecimal sumPR = p.add(r);
        BigDecimal two = new BigDecimal("2.0");
        BigDecimal f1score = two.multiply(p).multiply(r)
                            .divide(sumPR, new MathContext(4, RoundingMode.HALF_UP));
        return f1score;
    }


    public void printResults() {
        System.out.println("Counted results:");
        System.out.println("---> TP: " + this.tp);
        System.out.println("---> FP: " + this.fp);
        System.out.println("---> FN: " + this.fn);
        System.out.println();
        System.out.println("Precision: " + calculatePrecision());
        System.out.println("Recall:    " + calculateRecall());
        System.out.println("F1-Score:  " + calculateF1Score());
    }
}
