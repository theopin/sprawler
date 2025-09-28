import { BrowserRouter, Routes, Route, Link, Navigate } from 'react-router-dom';
import PdfViewer from './components/pdf/PdfViewer';
import MyInfoHandler from './components/myinfo/MyInfoHandler';

function App() {
  return (
    <BrowserRouter>
      <nav>
        <Link to="/">Home</Link> | <Link to="/myinfo">MyInfo</Link> | <Link to="/pdf">PDF Viewer</Link>
      </nav>
      <Routes>
        <Route path="/" element={<div>Welcome to the App</div>} />
        <Route path="/callback" element={<MyInfoHandler/>} />
        <Route path="/pdf" element={<PdfViewer />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
