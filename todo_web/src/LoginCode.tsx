
import { useContext, useEffect } from "react";
import { useAssertNotAuthorized, SetAuthContext } from "./auth";
import { useNavigate, useSearchParams } from "react-router-dom";

const useCode = () => {
    const [params] = useSearchParams();
    return params.get("code");
};

export default () => {
    const code = useCode();
    useAssertNotAuthorized();
    const setAuth = useContext(SetAuthContext);
    const navigate = useNavigate();

    useEffect(() => {
        if (code !== null) {
            const url = `${import.meta.env.TIM95BELL_TODO_AS_URL}/oauth2/token`;
            const controller = new AbortController();
            const codeChallengeVerifier = window.localStorage.getItem("codeChallengeVerifier");
            if (codeChallengeVerifier === null) {
                navigate("/login");
                return;
            }
            const now = Date.now();
            fetch(url, {
                signal: controller.signal,
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    Authorization: `Basic ${btoa(import.meta.env.TIM95BELL_TODO_AS_WEB_CLIENT_ID + ":" + import.meta.env.TIM95BELL_TODO_AS_WEB_CLIENT_SECRET)}`,
                },
                body: new URLSearchParams({
                    redirect_uri: `${import.meta.env.TIM95BELL_TODO_WEB_URL}/login/oauth2/code`,
                    grant_type: "authorization_code",
                    code: code,
                    code_verifier: codeChallengeVerifier,
                }),
            })
                .then((res) => res.status === 200 ? res.json() : Promise.reject("Invalid response http status code"))
                .then((body) => {
                    // TODO(TB): check shape of body
                    window.localStorage.removeItem("codeChallengeVerifier");
                    if (body
                        && typeof body.access_token === "string"
                        && typeof body.refresh_token === "string"
                        && typeof body.scope === "string"
                        && typeof body.id_token === "string"
                        && typeof body.expires_in === "number"
                    ) {
                        setAuth({
                            access_token: body.access_token,
                            refresh_token: body.refresh_token,
                            expires_at: now + body.expires_in,
                        });
                    } else {
                        return Promise.reject("Invalid response body");
                    }
                })
                .catch((err) => {
                    if (err !== "dismount") {
                        window.localStorage.removeItem("codeChallengeVerifier");
                        navigate("/login");
                    }
                });
            return () => controller.abort("dismount");
        } else {
            navigate("/login");
        }
    }, [code, navigate]);

    return null;
};
