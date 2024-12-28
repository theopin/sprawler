import { useState } from "react";
import { Worker, Viewer } from "@react-pdf-viewer/core";
import "@react-pdf-viewer/core/lib/styles/index.css";
import "@react-pdf-viewer/default-layout/lib/styles/index.css";
import { RequestHandler } from "../../utils/api/RequestHandler";

const PdfViewer = () => {
  const [pdfData, setPdfData] = useState(null);

  // Fetch PDF data from the API
  const fetchPdf = async () => {
    try {
      const response = await RequestHandler.getRequest("/pdf/simple");

      setPdfData(response.data);
    } catch (error) {
      console.error("Error fetching PDF:", error);
    }
  };

  // Convert Base64 string to Uint8Array for React PDF
  const base64ToUint8Array = (base64: string) => {
    const binaryString = atob(base64);
    const len = binaryString.length;
    const bytes = new Uint8Array(len);
    for (let i = 0; i < len; i++) {
      bytes[i] = binaryString.charCodeAt(i);
    }
    return bytes;
  };

  return (
    <div style={{ margin: "20px" }}>
      <button onClick={fetchPdf} style={{ marginBottom: "20px" }}>
        Fetch and Preview PDF
      </button>

      {pdfData ? (
        <div style={{ border: "1px solid #ccc", padding: "10px" }}>
          <Worker workerUrl={`https://unpkg.com/pdfjs-dist@3.11.174/build/pdf.worker.min.js`}>
            <Viewer fileUrl={URL.createObjectURL(new Blob([base64ToUint8Array(pdfData)], { type: "application/pdf" }))} />
          </Worker>
        </div>
      ) : (
        <p>No PDF loaded</p>
      )}
    </div>
  );
};

export default PdfViewer;