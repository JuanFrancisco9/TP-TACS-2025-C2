import { Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Statistics from './pages/Statistics'
import './App.css'

function App() {
    return (
        <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/estadisticas" element={<Statistics />} />
        </Routes>
    );
}

export default App;