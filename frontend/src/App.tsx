import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import MyInfo from './components/myinfo/MyInfo';
import PdfViewer from './components/pdf/PdfViewer';

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Home</Link> | <Link to="/myinfo">MyInfo</Link> | <Link to="/pdf">PDF Viewer</Link>
      </nav>
      <Routes>
        <Route path="/" element={<div>Welcome to the App</div>} />
        <Route path="/myinfo" element={<MyInfo />} />
        <Route path="/pdf" element={<PdfViewer />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
