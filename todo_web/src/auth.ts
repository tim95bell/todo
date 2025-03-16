
import { useEffect, useContext, createContext } from "react";

export type Auth = {
    access_token: string;
    refresh_token: string;
    scope: string;
    id_token: string;
    token_type: string;
    expires_in: string;
};

const codeChallengeCharacters = (() => {
    let result = "";
    for (let i = 0; i < 26; ++i) {
        result += String.fromCharCode("A".charCodeAt(0) + i);
        result += String.fromCharCode("a".charCodeAt(0) + i);
    }
    for (let i = 0; i < 10; ++i) {
        result += String.fromCharCode("0".charCodeAt(0) + i);
    }
    result += "-";
    result += ".";
    result += "_";
    result += "~";
    return result;
})();

const generateCodeChallengeVerifier = () => {
    const arr = new Uint8Array(64);
    window.crypto.getRandomValues(arr);
    return Array.from(arr, i => codeChallengeCharacters.charAt(i % codeChallengeCharacters.length)).join("");
};

const urlBase64Encode = (x: string): string =>
    btoa(x).replace(/\//g, '_').replace(/\+/g, '-').replace(/=/g, '');

const codeChallengeVerifierToCodeChallenge = (verifier: string): Promise<string> => {
    const data = new TextEncoder().encode(verifier);
    return window.crypto.subtle.digest("SHA-256", data)
        .then(x => String.fromCharCode(...new Uint8Array(x)))
        .then(x => urlBase64Encode(x));
};

export const generateCodeChallengeAndVerifier = () => {
    const verifier = generateCodeChallengeVerifier();
    return codeChallengeVerifierToCodeChallenge(verifier)
        .then(challenge => ({
            verifier,
            challenge,
        }));
};

export const AuthContext = createContext<Auth | null>(null);

export const SetAuthContext = createContext<
    React.Dispatch<React.SetStateAction<Auth | null>>
>({} as React.Dispatch<React.SetStateAction<Auth | null>>);

export const useAssertAuthorized = () => {
    const auth = useContext(AuthContext);
    useEffect(() => {
        console.assert(auth !== null);
    }, [auth]);
    return auth as Auth;
};

export const useAssertNotAuthorized = () => {
    const auth = useContext(AuthContext);
    useEffect(() => {
        console.assert(auth === null);
    }, [auth]);
};
