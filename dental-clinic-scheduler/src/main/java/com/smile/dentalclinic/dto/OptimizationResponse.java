package com.smile.dentalclinic.dto;

import java.util.List;

public class OptimizationResponse {
	
	private int recommendedOperatories;
    private long minimumCompletionHours;
    private List<OperatoryComparison> comparisons;

    public OptimizationResponse(
            int recommendedOperatories,
            long minimumCompletionHours,
            List<OperatoryComparison> comparisons) {

        this.recommendedOperatories = recommendedOperatories;
        this.minimumCompletionHours = minimumCompletionHours;
        this.comparisons = comparisons;
    }

    public int getRecommendedOperatories() {
        return recommendedOperatories;
    }

    public long getMinimumCompletionHours() {
        return minimumCompletionHours;
    }

    public List<OperatoryComparison> getComparisons() {
        return comparisons;
    }

    public static class OperatoryComparison {

        private int numberOfOperatories;
        private long completionTimeHours;
        private double improvementPercentage;

        public OperatoryComparison(
                int numberOfOperatories,
                long completionTimeHours,
                double improvementPercentage) {

            this.numberOfOperatories = numberOfOperatories;
            this.completionTimeHours = completionTimeHours;
            this.improvementPercentage = improvementPercentage;
        }

        public int getNumberOfOperatories() {
            return numberOfOperatories;
        }

        public long getCompletionTimeHours() {
            return completionTimeHours;
        }

        public double getImprovementPercentage() {
            return improvementPercentage;
        }
    }

}
