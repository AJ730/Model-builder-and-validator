import { useEffect, useState } from "react";
import { useMsal, useAccount } from "@azure/msal-react";
import { InteractionRequiredAuthError } from "@azure/msal-browser";

/**
 * Creates a hook which returns an access token given the scope
 * @param {String} scopes The scopes of the endpoint to be called
 * @returns an object with the access token and the account object for use in callApiWithToken() 
 */
 const useGetToken = scopes => {
    const { instance, accounts, inProgress } = useMsal();
    const account = useAccount(accounts[0] || {});
    const [token, setToken] = useState(null);

    useEffect(() => {
        if (account && inProgress === "none" && !token) {
            instance.acquireTokenSilent({
                scopes: scopes,
                account: account
            }).then(response => {
                setToken(response.accessToken);   
            }).catch((error) => {
                // in case if silent token acquisition fails, fallback to an interactive method
                if (error instanceof InteractionRequiredAuthError) {
                    if (account && inProgress === "none") {
                        instance.acquireTokenPopup({
                            scopes: scopes,
                        }).then(response => {
                            setToken(response.accessToken); 
                        }).catch(error => console.log(error));
                    }
                }
            });
        }  
        // eslint-disable-next-line 
    }, [])
    
    
  
    return {account, token};
};

export default useGetToken;