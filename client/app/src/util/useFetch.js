import { useEffect, useState } from "react";
import { useMsal, useAccount } from "@azure/msal-react";
import { InteractionRequiredAuthError } from "@azure/msal-browser";


/**
 * Call the API endpoint of the application to request data after an access token has been obtained.
 * @param {String} accessToken The token obtained from calling the authorization server 
 * @param {String} apiEndpoint The API endpoint to call
 * @param {Object} account The account object to get the oid from
 * @param {String} format The format of the requested resource
 * @param {String} method The method of the HTTP request
 * @param {FormData} body The body of the request (e.g. FormData)
 * @returns The data in a JSON format
 */
export const callApiWithToken = async (accessToken, apiEndpoint, account, format, method, body, headerObj) => {
    const headers = headerObj ? headerObj : new Headers();
    
    const bearer = `Bearer ${accessToken}`;

    headers.append("Authorization", bearer);
    headers.append("oid", account.idTokenClaims.oid)


    const options = {
        method: method,
        headers: headers
    };

    if (body) options.body = body

    //Returns a url to access the blob, we can then use it in "src" properties 
    //in <video> for example
    if (format === "blob") {
        return fetch(apiEndpoint, options)
            .then(response => response.blob())
            .then(blob => URL.createObjectURL(blob))
            .catch(error => console.log(error));
    } else if (format === "json") {
        return fetch(apiEndpoint, options)  
            .then(response => response.json())
            .catch(error => console.log(error));
    } 
    else {
        return fetch(apiEndpoint, options)  
            .catch(error => console.log(error));
    }
}


/**
 * Creates a hook which fetch data from the provided endpoint and scopes.
 * @param {String} scopes The scopes of the endpoint
 * @param {String} endpoint The API endpoint to call
 * @param {String} format The format of the requested resource
 * @param {String} method The method of the HTTP request
 * @returns 
 */
const useFetch = (scopes, endpoint, format = "json", method = "GET") => {
    const { instance, accounts, inProgress } = useMsal();
    const account = useAccount(accounts[0] || {});
    const [data, setData] = useState(null);

    useEffect(() => {
        if (account && inProgress === "none" && !data) {
            instance.acquireTokenSilent({
                scopes: scopes,
                account: account
            }).then((response) => {
                callApiWithToken(response.accessToken, endpoint, account, format, method)
                    .then(response => setData(response));
            }).catch((error) => {
                // in case if silent token acquisition fails, fallback to an interactive method
                if (error instanceof InteractionRequiredAuthError) {
                    if (account && inProgress === "none") {
                        instance.acquireTokenPopup({
                            scopes: scopes,
                        }).then((response) => {
                            callApiWithToken(response.accessToken, endpoint, account, format, method)
                                .then(response => setData(response));
                        }).catch(error => console.log(error));
                    }
                }
            });
        }  
        // eslint-disable-next-line 
    }, [])
    
    
  
    return data;
};

 
export default useFetch;