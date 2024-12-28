import { useState } from "react";
import { Document, Page } from "react-pdf";
import { RequestHandler } from "../../utils/api/RequestHandler";
import { pdfjs } from 'react-pdf';

import "react-pdf/dist/esm/Page/AnnotationLayer.css"; // For annotations
import "react-pdf/dist/esm/Page/TextLayer.css"; // For text rendering

pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

const PdfViewer = () => {
  const [pdfData, setPdfData] = useState<string | null>(null); // Base64 PDF data
  const [pageNumber, setPageNumber] = useState(1); // Track the current page number
  const [numPages, setNumPages] = useState<number | null>(null); // Total number of pages

  // Fetch PDF data from the API
  const fetchPdf = async () => {
    try {
      const response = await RequestHandler.getRequest("/pdf/simple");
      setPdfData(response.data); // Store the base64 data
    } catch (error) {
      console.error("Error fetching PDF:", error);
    }
  };

  // Handle the successful loading of the PDF
  const onLoadSuccess = ({ numPages }: { numPages: number }) => {
    setNumPages(numPages);
  };

  // Navigate to the previous page
  const goToPrevPage = () => {
    if (pageNumber > 1) {
      setPageNumber(pageNumber - 1);
    }
  };

  // Navigate to the next page
  const goToNextPage = () => {
    if (pageNumber < (numPages || 1)) {
      setPageNumber(pageNumber + 1);
    }
  };

  return (
    <div style={{ margin: "20px" }}>
      <button onClick={fetchPdf} style={{ marginBottom: "20px" }}>
        Fetch and Preview PDF
      </button>

      {pdfData ? (
        <div style={{ border: "1px solid #ccc", padding: "10px" }}>
          <Document
            file={`data:application/pdf;base64,${pdfData}`}
            onLoadSuccess={onLoadSuccess}
            loading={<p>Loading PDF...</p>}
          >
            <Page pageNumber={pageNumber} />
          </Document>

          <div>
            <button onClick={goToPrevPage} disabled={pageNumber <= 1}>
              Prev
            </button>
            <button onClick={goToNextPage} disabled={pageNumber >= (numPages || 1)}>
              Next
            </button>
            <p>Page {pageNumber} of {numPages}</p>
          </div>
        </div>
      ) : (
        <p>No PDF loaded</p>
      )}
    </div>
  );
};

export default PdfViewer;
