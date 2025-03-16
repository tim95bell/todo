
import { useState, useEffect } from "react";
import { Routes, Route, Navigate } from "react-router-dom";
import { useAssertNotAuthorized, generateCodeChallengeAndVerifier } from "./auth";
import LoginCode from "./LoginCode";

const buildAuthorizeUrl = (codeChallenge: string) => `${import.meta.env.TIM95BELL_TODO_AS_URL}/oauth2/authorize` +
    `?response_type=code` +
    `&client_id=${import.meta.env.TIM95BELL_TODO_AS_WEB_CLIENT_ID}` +
    `&scope=openid` +
    `&redirect_uri=${import.meta.env.TIM95BELL_TODO_WEB_URL}/login/oauth2/code` +
    `&code_challenge=${codeChallenge}` +
    `&code_challenge_method=S256`;

export default () => {
    useAssertNotAuthorized();
    const [authorizeUrl, setAuthorizeUrl] = useState<string | undefined>(undefined);
    useEffect(() => {
        generateCodeChallengeAndVerifier()
            .then(({challenge, verifier}) => {
                window.localStorage.setItem("codeChallengeVerifier", verifier);
                setAuthorizeUrl(buildAuthorizeUrl(challenge));
            });
    }, [setAuthorizeUrl]);

    return (
        <div>
            <h1>Login</h1>
            <a href={authorizeUrl}>Login</a>
            <Routes>
                <Route path="/" element={null} />
                <Route path="/oauth2/code" Component={LoginCode} />
                <Route path="*" element={<Navigate replace to="/" relative="path" />} />
            </Routes>
        </div>
    );
};
