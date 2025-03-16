
import { Navigate, Route, Routes } from "react-router-dom";
import Home from "./Home";
import Login from "./Login";

export default ({ isLoggedIn }: { isLoggedIn: boolean }) => (
    <Routes>
        {isLoggedIn ? (
            <>
                <Route path="/" Component={Home} />
                <Route path="*" element={<Navigate to="/" />} />
            </>
        ) : (
            <>
                <Route path="/login/*" Component={Login} />
                <Route path="*" element={<Navigate to="/login" />} />
            </>
        )}
    </Routes>
);
