import React from 'react';
import MUIDataTable from "mui-datatables";

export const PredictionDisplay = ({data, tableName, videoPlayerRef, fps}) => {
  const columns = [
    {
      label: 'Frame Number',
      name: 'frameNum',
      options: {
        filter: false,
        sort: true,
       }
    },
    {
      label: 'Object ID',
      name: 'objectId',
      options: {
        filter: false,
       }
    },
    {
      label: 'Label',
      name: 'label',
      options: {
        filter: true,
       }
    },
    {
      label: 'Model Confidence',
      name: 'modelConfidence',
      options: {
        filter: false,
       }
    },
    {
      label: 'Tracker Confidence',
      name: 'trackerConfidence',
      options: {
        filter: false,
       }
    },
  ]
  const options = {
    filterType: "checkbox",
    onRowClick: (rowData) => {
      const frameNum = rowData[0]
      videoPlayerRef.current.currentTime = frameNum/fps + 0.0001
    }
  };

  return ( 
    <MUIDataTable
      title={tableName}
      data={data}
      columns={columns}
      options={options}
    />
   );
}
 
export default PredictionDisplay;
