
import { useState, useMemo } from "react";
import {
    BrowserRouter,
} from "react-router-dom";
import { Auth, AuthContext, SetAuthContext } from "./auth";
import AppRoutes from "./AppRoutes";

function App() {
    const [auth, setAuth] = useState<Auth | null>(null);
    const isLoggedIn = useMemo(() => {
        return auth !== null;
    }, [auth]);

    return (
        <AuthContext.Provider value={auth}>
            <SetAuthContext.Provider value={setAuth}>
                <BrowserRouter>
                    <AppRoutes isLoggedIn={isLoggedIn} />
                </BrowserRouter>
            </SetAuthContext.Provider>
        </AuthContext.Provider>
    );
}

export default App;
