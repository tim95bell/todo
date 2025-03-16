
import { useAssertAuthorized } from "./auth";

export default () => {
    const auth = useAssertAuthorized();

    return (
        <div>
            <h1>Home</h1>
            <h1>Auth Token</h1>
            <p>{auth.access_token}</p>
        </div>
    );
};
