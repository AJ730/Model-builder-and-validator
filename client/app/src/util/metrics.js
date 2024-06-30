import * as tf from "@tensorflow/tfjs";
import * as tfvis from "@tensorflow/tfjs-vis"


/**
 * Transpose a 2D array
 * @param {Array} array a 2D array  
 * @returns a transposed 2D array
 */
export const transpose = (array) => {
    return array[0].map((_, colIndex) => array.map(row => row[colIndex]))
}

/**
 * Compare the object id of the two objects for sorting purposes
 * @param {Object} firstEl first object
 * @param {Object} secondEl second object
 * @returns {Number} a number
 */
const compareObjectId = ((firstEl, secondEl) => {
    return firstEl.objectId - secondEl.objectId;
})

/**
 * Calculate the precision - proportion of predicted positives
 * of a class
 *  (TP/(TP + FP)) 
 * @param {Array} classLabels all the possible labels
 * @param {String} label the label to calculate the precision for
 * @param {Tensor2D} confusionMatrix the confusion matrix
 * @returns {Number} the precision of the label 
*/
export const calculatePrecision = (classLabels, label, confusionMatrix) => {
    const index = [classLabels.indexOf(label)]
    const labelPredictions = confusionMatrix.gather(index, 1)

    const truePositives =  labelPredictions.gather(index).arraySync()[0][0]
    const falsePositives = labelPredictions.sum().arraySync() - truePositives
   
    return truePositives / (truePositives + falsePositives)
}


/**
 * Calculate the recall - proportion of actual positives
 * of a class
 * (TP/(TP + FN))
 * @param {Array} classLabels all the possible labels
 * @param {String} label the label to calculate the recall for
 * @param {Tensor2D} confusionMatrix the confusion matrix
 * @returns {Number} the recall of the label
 */
export const calculateRecall = (classLabels, label, confusionMatrix) => {
    const index = [classLabels.indexOf(label)]
    const actualLabels = confusionMatrix.gather(index)

    const truePositives =  actualLabels.arraySync()[0][index[0]]
    const falseNegatives = actualLabels.sum().arraySync() - truePositives
   
    return truePositives / (truePositives + falseNegatives)
}


/**
 * Calculate the F1 per class
 * @param {Number} precision the precision of the class
 * @param {Number} recall the recall of the class
 * @returns {Number} the F1 of the label
 */
export const calculateF1 = (precision, recall) => {  

    if( Number.isNaN (precision)|| Number.isNaN(recall)) return NaN
    return 2 * (precision * recall) / (precision + recall)
}

/**
 * Calculate the Intersection over Union (IOU) between two boxes
 * @param {Object} box1 the first box
 * @param {Object} box2 the second box
 * @returns the IOU between the two boxes
 */
export const IOU = (box1, box2) => {
    const {x1, y1, x2, y2} = {x1: box1.trackerL, y1: box1.trackerT, x2: box1.trackerL + box1.trackerW, y2: box1.trackerT + box1.trackerH}
    const {x3, y3, x4, y4} = {x3: box2.trackerL, y3: box2.trackerT, x4: box2.trackerL + box2.trackerW, y4: box2.trackerT + box2.trackerH}

    const xInter1 = Math.max(x1, x3)
    const yInter1 = Math.max(y1, y3)
    const xInter2 = Math.min(x2, x4)
    const yInter2 = Math.min(y2, y4)

    const widthInter = Math.abs(xInter2 - xInter1)
    const heightInter = Math.abs(yInter2 - yInter1)
    const areaInter = widthInter * heightInter

    const areaBox1 = box1.trackerW * box1.trackerH
    const areaBox2 = box2.trackerW * box2.trackerH

    const areaUnion = areaBox1 + areaBox2 - areaInter
    const iou = areaInter / areaUnion
   
    return iou
}


/**
 * Calculalte the average IOU
 * @param {Array} classLabels an array with all the class labels 
 * @param {Array} prediction array of objects predicted by the model
 * @param {Array} actual array of objects created by the user
 * @returns the average IOU
 */
const calculateIOU = (prediction, actual) => {
    const result = [0, 0]

    actual.forEach((x, index) => {
        if(x.label !== "false_detection" && x.label !== "unlabelled" && index < prediction.length) {
            result[0] += IOU(x, prediction[index])
            result[1] += 1
        } else if (x.label !== "unlabelled") {
            result[0] += 0
            result[1] += 1
        }
    })

    return result[0] / result[1]
}


/**
 * Calculate the macro averages for F1, precision, and recall
 * @param {Array} perClassF1 an array with the F1 score of all the classes
 * @param {Array} precisionAndRecall an array with the precision and recall of all the classes
 * @param {Number} labelCount the number of possible classes/labels (i.e. those with Predicted or Actual counts 
 * that are larger than 0)
 * @returns an object with the results for Macro F1, precision, and recall
 */
export const calculateMacro = (perClassF1, precisionAndRecall, labelCount) => {
    const macroF1 = perClassF1.reduce((x, y) => x + (Number.isNaN(y) ? 0 : y), 0) / labelCount
    const macroPrecision = precisionAndRecall.reduce((x, y) => x + (Number.isNaN(y[0]) ? 0 : y[0]), 0) / labelCount
    const macroRecall = precisionAndRecall.reduce((x, y) =>  x + (Number.isNaN(y[1]) ? 0 : y[1]), 0) / labelCount

    return {f1:macroF1, precision:macroPrecision, recall:macroRecall}
}

/**
 * Calculate the weighted averages for F1, precision, and recall
 * @param {Array} perClassF1 an array with the F1 score of all the classes
 * @param {Array} precisionAndRecall an array with the precision and recall of all the classes
 * @param {Array} perClassAccuracy the array with the accuracy and count for each object
 * @param {Number} sampleCount the number of samples
 * @returns an object with the results for Weighted F1, precision, and recall
 */
export const calculateWeighted = (perClassF1, precisionAndRecall, perClassAccuracy, sampleCount) => {
    const weightedF1 = perClassF1
        .reduce((x, y, index) => x + (Number.isNaN(y) ? 0 : y) * perClassAccuracy[index].count, 0) / sampleCount

    const weightedPrecision = precisionAndRecall
        .reduce((x, y, index) => x + (Number.isNaN(y[0]) ? 0 : y[0]) * perClassAccuracy[index].count, 0) / sampleCount

    const weightedRecall = precisionAndRecall
        .reduce((x, y, index) => x + (Number.isNaN(y[1]) ? 0 : y[1]) * perClassAccuracy[index].count, 0) / sampleCount

    return {f1:weightedF1, precision:weightedPrecision, recall:weightedRecall}
}

/**
 * Create the count comparison metrics from existing metrics
 * @param {Array} classLabels an array with all the class labels 
 * @param {Object} accuracy an object with the accuracy and accuracy per class  
 * @param {Array} undetectedObjects objects not detected by the model
 * @param {Array} predictionCounts the counts of from the model's predictions
 * @param {Array} modelPredictions predicted labels of the objects
 * @param {Array} reclassifiedObjects actual labels of the objects
 * @returns a table with all the count comparison data
 */
export const createCountComparison = (classLabels, accuracy, undetectedObjects, predictionCounts, reclassifiedObjects) => {
    
    let actualCounts = accuracy.perClassAccuracy
        .map(x => x.count)
        .concat(reclassifiedObjects.length + (undetectedObjects ? undetectedObjects.length : 0))
    if (undetectedObjects) undetectedObjects
        .forEach(o => ++actualCounts[classLabels.indexOf(o.label)])
    const delta = predictionCounts.map((x, index) => actualCounts[index] - x)

    return transpose([actualCounts, predictionCounts, delta])
}

/**
 * Calculate the following metrics: confusion matrix, precision, recall,
 * F1, accuracy, and count comparison
 * @param {Array} classLabels an array with all the class labels 
 * @param {Array} modelPredictions prediction of the Computer Vision model
 * @param {Array} actualLabels labels after correction by the user
 * @param {Array} undetectedObjects objects not detected by the model
 * @returns {Object} an object with a confusion matrix atrribute and results of calculating 
 * precision and recall
 */
export const calculateMetrics = async (classLabels, modelPredictions, actualLabels) => {
    const detectedCount = modelPredictions.length
    
    const prediction = [...modelPredictions].sort(compareObjectId)
    const actual = [...actualLabels].sort(compareObjectId)
        
    const predictedObjects = tf.tensor1d(prediction.map(x => classLabels.indexOf(x.label)), 'int32')
    const reclassifiedObjects = tf.tensor1d(actual.slice(0, modelPredictions.length).map(x => classLabels.indexOf(x.label)), 'int32')
    const undetectedObjects = actual.slice(modelPredictions.length)

    const confusionMatrix = tf.math.confusionMatrix(reclassifiedObjects, predictedObjects, classLabels.length)

    const perClassAccuracy = await tfvis.metrics.perClassAccuracy(reclassifiedObjects, predictedObjects, classLabels.length)
    const overallAccuracy = await tfvis.metrics.accuracy(reclassifiedObjects, predictedObjects)
    const accuracy = {overallAccuracy, perClassAccuracy}

    const precisionAndRecall = classLabels.map(label => 
        [
         calculatePrecision(classLabels, label, confusionMatrix),     
         calculateRecall(classLabels, label, confusionMatrix)
        ]
    )

    const predictionCounts = (await tfvis.metrics.perClassAccuracy(predictedObjects, predictedObjects, classLabels.length))
                                .map(x => x.count).concat(modelPredictions.length)
    const countComparison = createCountComparison(classLabels, accuracy, 
                undetectedObjects, predictionCounts, reclassifiedObjects.arraySync())

    // Count the number of labels that are present in the video (either detected by the model or added by the user).
    // We do not count the two special classes.
    const labelCount = perClassAccuracy
        .slice(0, perClassAccuracy.length - 2)
        .reduce((x, y, index) => (y.count === 0 && predictionCounts[index] === 0) ? x + 0 : x + 1, 0)
    
    const perClassF1 = precisionAndRecall.map(x => calculateF1(x[0], x[1]))
    const macroAverages = calculateMacro(perClassF1, precisionAndRecall, labelCount)
    const weightedAverages = calculateWeighted(perClassF1, precisionAndRecall, perClassAccuracy, detectedCount)

    const falseDetectionCount = perClassAccuracy[classLabels.indexOf("false_detection")].count
    const vennDiagram = [falseDetectionCount, 
                        actualLabels.length - falseDetectionCount - undetectedObjects.length,
                        undetectedObjects.length]

    const IOU = calculateIOU(prediction, actual)
                        
    return {confusionMatrix, precisionAndRecall, perClassF1, macroAverages, weightedAverages, countComparison, vennDiagram, IOU}
}

/**
 * Display all the metrics in an unintrusive dashboard on the same page
 * @param {Array} classLabels all class labels of the container
 * @param {Array} modelPredictions prediction of the Computer Vision model
 * @param {Array} actualLabels labels after correction by the user provided that 
 * the object id is unique and increasing
 * @param {Array} undetectedObjects objects not detected by the model
 */
const displayMetrics = async (classLabels, modelPredictions, actualLabels) => {

    //Calculate all metrics
    const {confusionMatrix, precisionAndRecall, perClassF1, macroAverages, weightedAverages, 
            countComparison, vennDiagram, IOU} = await calculateMetrics(classLabels, modelPredictions, actualLabels)
        
    //Create displays for all metrics
    const precisionDisplay = precisionAndRecall.map(x => (x[0] * 100).toFixed(2) + "%")
    const recallDisplay =  precisionAndRecall.map(x => (x[1] * 100).toFixed(2) + "%")
    const perClassF1Display = perClassF1.map(x => (x * 100).toFixed(2) + "%")
    const countsDisplay = countComparison.map((x, index) => [classLabels[index]].concat(x))
    const averagesDisplay =  [
            ["Macro Average"].concat(Object.values(macroAverages).map(x => (x * 100).toFixed(2) + "%")),
            ["Weighted Average"].concat(Object.values(weightedAverages).map(x => (x * 100).toFixed(2) + "%"))]

    //Create the surfaces to display the metrics
    const confusionTab = 'Confusion Matrix'
    const F1AndOthersTab = 'F1, Precision & Recall'
    const countComparisonTab = 'Count Comparison'
    const vennTab = 'Venn Diagram Information'
    const iouTab = "Intersection Over Union"

    const width = 1300
    const confusionSurface = {
        name: 'Confusion Matrix', 
        tab: confusionTab,
        styles: {
            width: width,
         }
    };
    const precisionSurface = {
        name: 'Precision Per Class', 
        tab: F1AndOthersTab, 
        styles: {
            width: width
         }
    };
    const recallSurface = {
        name: 'Recall Per Class', 
        tab: F1AndOthersTab, 
        styles: {
            width: width
         }
    };
    const f1Surface = {
        name: 'F1 Per Class',
        tab: F1AndOthersTab,
        styles: {
            width: width
        }
    }
    const averagesSurface = {
        name: 'Average Scores',
        tab: F1AndOthersTab,
        styles: {
            width: width
        }
    }
    const countsSurface = {
        name: 'Count Comparison',
        tab: countComparisonTab,
        styles: {
            width: width
        }
    }
    const vennSurface = {
        name: 'Venn Diagram Information',
        tab: vennTab,
        styles: {
            width: width
        }
    }
    const iouSurface = {
        name: 'Average Intersection Over Union',
        tab: iouTab,
    }
    
    //Render all metrics
    tfvis.visor().open()
    
    if (!tfvis.visor().isFullscreen()) tfvis.visor().toggleFullScreen()
    tfvis.render.table(countsSurface, {headers: ["","Actual", "Prediction", "Delta"], values: countsDisplay})
    tfvis.render.table(vennSurface, {headers: ["Detected but not Labelled (False Detection)",
             "Detected and Labelled", "Labelled but not Detected (User Added Boxes)"], values: [vennDiagram]})
    tfvis.render.table(iouSurface, {headers: ["IOU"], values: [[(IOU*100).toFixed(2)+"%"]] })         
    tfvis.render.table(averagesSurface, {headers: ["", "F1", "Precision", "Recall"], values: averagesDisplay})
    tfvis.render.table(f1Surface, {headers: classLabels, values: [perClassF1Display]})
    tfvis.render.table(precisionSurface, {headers: classLabels, values: [precisionDisplay]})
    tfvis.render.table(recallSurface, {headers: classLabels, values: [recallDisplay]})
    
    tfvis.render.confusionMatrix(confusionSurface, {values: confusionMatrix.arraySync(), tickLabels: classLabels}, {
        shadeDiagonal: false
    });

}

export default displayMetrics