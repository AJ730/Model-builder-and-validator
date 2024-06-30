import { useEffect, useState } from "react";
import ReactExport from "react-data-export";
import { calculateMetrics } from "../../util/metrics";
import { v4 as uuidv4 } from 'uuid';
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';

/**
 * Generate a new Excel sheet given the formatted data and the sheet's name
 * @param {*} ExcelSheet ExcelSheet component
 * @param {*} ExcelColumn ExcelColumn component
 * @param {*} data the data of the sheet
 * @param {String} name the name of the sheet
 * @returns a new ExcelSheet with columns as specified in the data
 */
const generateNewSheet = (ExcelSheet, ExcelColumn, data, name) => {
    
    return (
        <ExcelSheet data={data} name={name}>
            {Object.keys(data[0]).map(x => <ExcelColumn label = {x} value = {x} key = {uuidv4()}></ExcelColumn>)}
        </ExcelSheet>
    )
}


const ExportMetrics =  ({classLabels, modelPredictions, actualLabels}) => {

    //This creates a custom tooltip with the given styles.
    const CstmTooltip = withStyles((theme) => ({
        tooltip: {
            backgroundColor: '#f5f5f9',
            color: 'rgba(0, 0, 0, 1)',
            maxWidth: 500,
            fontSize: theme.typography.pxToRem(12),
            border: '1px solid #dadde9',
        },
    }))(Tooltip);

    const [metrics, setMetrics]= useState(null)

    useEffect(() => {
        const getMetrics = async () => {
            const metrics = await calculateMetrics(classLabels, modelPredictions, actualLabels)
            setMetrics(metrics);
        }
        getMetrics()
        // eslint-disable-next-line 
    }, [actualLabels])

    if (metrics) {

        const ExcelFile = ReactExport.ExcelFile;
        const ExcelSheet = ReactExport.ExcelFile.ExcelSheet;
        const ExcelColumn = ReactExport.ExcelFile.ExcelColumn;


        const {confusionMatrix, precisionAndRecall, perClassF1, macroAverages,
             weightedAverages, countComparison, vennDiagram, IOU} = metrics

        //Transform the data to the appropriate format for Excel sheet
        const confusionData = confusionMatrix
                                .arraySync()
                                .map((row, index) => Object.assign({label : classLabels[index]}, 
                                    row
                                    .reduce((x, y, index) => ({
                                        ...x, [classLabels[index]]: y
                                    }), {})
                                ))

        const countData = countComparison.map((row, index) => Object.assign({label : classLabels[index]}, 
            {
                actual: row[0],
                prediction: row[1],
                delta: row[2]
            }
        ))

        const averageScoreData = [Object.assign({averages : "macro averages"}, macroAverages), 
                                  Object.assign({averages : "weighted averages"}, weightedAverages)]
        const precisionData = Object.assign({score : "precision"}, 
                                precisionAndRecall.reduce((x, y, index) => ({
                                    ...x, [classLabels[index]]: Number.isNaN(y[0]) ? "NaN" : y[0]
                                }), {}))
        const recallData = Object.assign({score : "recall"}, 
                            precisionAndRecall.reduce((x, y, index) => ({
                                ...x, [classLabels[index]]: Number.isNaN(y[1]) ? "NaN" : y[1]
                            }), {}))
        const f1Data = Object.assign({score : "f1"}, 
                        perClassF1.reduce((x, y, index) => ({
                            ...x, [classLabels[index]]: Number.isNaN(y) ? "NaN" : y
                        }), {}))
        const perClassScoreData = [f1Data, precisionData, recallData]
        const vennData = [
            {"Detected but not Labelled (False Detection)": vennDiagram[0],
             "Detected and Labelled": vennDiagram[1],
             "Labelled but not Detected (User Added Boxes)": vennDiagram[2]
            }]
        const iouData = [{"Average IOU": IOU}]
                
        return ( 
            <ExcelFile element = {
                <CstmTooltip title="Make sure to save before exporting to see your changes!.">
                    <button style={{ position:'absolute', right:'0%' , width: '150px' }} type='button' className="apbtn">Export Metrics</button>
                </CstmTooltip>}>
                {generateNewSheet(ExcelSheet, ExcelColumn, confusionData, "Confusion Matrix")}
                {generateNewSheet(ExcelSheet, ExcelColumn, averageScoreData, "F1, Precision, Recall Averages")}
                {generateNewSheet(ExcelSheet, ExcelColumn, perClassScoreData, "F1, Precision, Recall Per Class")}
                {generateNewSheet(ExcelSheet, ExcelColumn, countData, "Count Comparison")}
                {generateNewSheet(ExcelSheet, ExcelColumn, vennData, "Venn Diagram Information")}
                {generateNewSheet(ExcelSheet, ExcelColumn, iouData, "Average IOU")}
            </ExcelFile> 
        );
    } else {
        return null
    }
}
 
export default ExportMetrics;