import * as tf from "@tensorflow/tfjs";
import { calculateF1, calculateMacro, calculatePrecision, calculateRecall, calculateWeighted, transpose } from "../util/metrics";
import '@testing-library/jest-dom';
import data from "../data/metricsTestData.json"
import * as tfvis from "@tensorflow/tfjs-vis"
import{createCountComparison} from "../util/metrics"

const labels = tf.tensor1d(data.labels)
const predictions = tf.tensor1d(data.predictions)
const classLabels = ["Cat", "Fish", "Hen"]

/**
 * Row: Label
 * Column: Predicted
 *  [[4, 1, 1],
 *   [6, 2, 2],
 *   [3, 0, 6]]
 */
const confusionMatrix = tf.tensor2d([4, 1, 1, 6, 2, 2, 3, 0, 6], [3, 3])
var perClassAccuracy = null;
(tfvis.metrics.perClassAccuracy(labels, predictions)).then(data => {perClassAccuracy = data});
var perfectAccuracy = null;
(tfvis.metrics.perClassAccuracy(predictions, predictions)).then(data => {perfectAccuracy = data});

test('calculate precision Cat', () => {
    expect(calculatePrecision(classLabels, "Cat", confusionMatrix)).toBe(4/13) 
});

test('calculate precision Fish', () => {
    expect(calculatePrecision(classLabels, "Fish", confusionMatrix)).toBe(2/3) 
});

test('calculate precision Hen', () => {
    expect(calculatePrecision(classLabels, "Hen", confusionMatrix)).toBe(6/9) 
});

test('calculate recall Cat', () => {
    expect(calculateRecall(classLabels, "Cat", confusionMatrix)).toBe(4/6) 
});

test('calculate precision Fish', () => {
    expect(calculateRecall(classLabels, "Fish", confusionMatrix)).toBe(2/10) 
});

test('calculate precision Hen', () => {
    expect(calculateRecall(classLabels, "Hen", confusionMatrix)).toBe(6/9) 
});

test('calculate F1 Cat', () => {
    const precision = calculatePrecision(classLabels, "Cat", confusionMatrix)
    const recall = calculateRecall(classLabels, "Cat", confusionMatrix)
    expect(parseFloat(calculateF1(precision, recall).toFixed(3))).toBe(0.421) 
});

test('calculate F1 Fish', () => {
    const precision = calculatePrecision(classLabels, "Fish", confusionMatrix)
    const recall = calculateRecall(classLabels, "Fish", confusionMatrix)
    expect(parseFloat(calculateF1(precision, recall).toFixed(3))).toBe(0.308) 
});

test('calculate F1 Hen', () => {
    const precision = calculatePrecision(classLabels, "Hen", confusionMatrix)
    const recall = calculateRecall(classLabels, "Hen", confusionMatrix)
    expect(parseFloat(calculateF1(precision, recall).toFixed(3))).toBe(0.667) 
});

test('calculate Macro', () => {
    const confusionMatrix = tf.math.confusionMatrix(labels, predictions, classLabels.length);

    const precisionAndRecall = classLabels.map(label => 
        [calculatePrecision(classLabels, label, confusionMatrix), calculateRecall(classLabels, label, confusionMatrix)])

    const perClassF1 = precisionAndRecall.map(x => calculateF1(x[0], x[1]))
    const macros = calculateMacro(perClassF1, precisionAndRecall, classLabels.length)

    expect(parseFloat(macros.precision.toFixed(3))).toBe(0.547)
    expect(parseFloat(macros.recall.toFixed(3))).toBe(0.511)
    expect(parseFloat(macros.f1.toFixed(3))).toBe(0.465)
})

test('calculate Weighted', () => {
    const confusionMatrix = tf.math.confusionMatrix(labels, predictions, classLabels.length);
    
    const precisionAndRecall = classLabels.map(label => 
        [calculatePrecision(classLabels, label, confusionMatrix), calculateRecall(classLabels, label, confusionMatrix)])

    const perClassF1 = precisionAndRecall.map(x => calculateF1(x[0], x[1]))
    const weighted = calculateWeighted(perClassF1, precisionAndRecall, perClassAccuracy, data.labels.length)

    
    expect(parseFloat(weighted.precision.toFixed(3))).toBe(0.581)
    expect(parseFloat(weighted.recall.toFixed(3))).toBe(0.480)
    expect(parseFloat(weighted.f1.toFixed(3))).toBe(0.464)
})

test('calculate Count Comparison', () => {

    const accuracy = {perClassAccuracy}
    const predictionCounts = perfectAccuracy.map(x => x.count).concat(25)

    const actualCount = transpose([
                            [6,  10, 9, 25], 
                            [13, 3,  9, 25],
                            [-7, 7,  0,  0]
                        ])
    const countComparison = createCountComparison(classLabels, accuracy, null, predictionCounts, labels.arraySync())
    expect(countComparison).toStrictEqual(actualCount)
})

  