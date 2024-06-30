/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

import { LogLevel } from "@azure/msal-browser";

const SERVER_HOST = process.env.REACT_APP_Server

/**
 * Configuration object to be passed to MSAL instance on creation. 
 * For a full list of MSAL.js configuration parameters, visit:
 * https://github.com/AzureAD/microsoft-authentication-library-for-js/blob/dev/lib/msal-browser/docs/configuration.md 
 */
export const msalConfig = {
    auth: {
        clientId: process.env.REACT_APP_Clientid, // This is the ONLY mandatory field that you need to supply.
        authority:  process.env.REACT_APP_Authority, // Defaults to "https://login.microsoftonline.com/common"
        redirectUri: "/", // You must register this URI on Azure Portal/App Registration. Defaults to window.location.origin
        postLogoutRedirectUri: "/", // Indicates the page to navigate after logout.
        navigateToLoginRequestUrl: false, // If "true", will navigate back to the original request location before processing the auth code response.
    },
    cache: {
        cacheLocation: "sessionStorage", // Configures cache location. "sessionStorage" is more secure, but "localStorage" gives you SSO between tabs.
        storeAuthStateInCookie: false, // Set this to "true" if you are having issues on IE11 or Edge
    },
    system: {
        loggerOptions: {
            loggerCallback: (level, message, containsPii) => {
                if (containsPii) {
                    return;
                }
                switch (level) {
                    case LogLevel.Error:
                        console.error(message);
                        return;
                    case LogLevel.Info:
                        console.info(message);
                        return;
                    case LogLevel.Verbose:
                        console.debug(message);
                        return;
                    case LogLevel.Warning:
                        console.warn(message);
                        return;
                    default:
                        return;
                }
            }
        }
    }
};

/**
 * Scopes you add here will be prompted for user consent during sign-in.
 * By default, MSAL.js will add OIDC scopes (openid, profile, email) to any login request.
 * For more information about OIDC scopes, visit: 
 * https://docs.microsoft.com/en-us/azure/active-directory/develop/v2-permissions-and-consent#openid-connect-scopes
 */
export const loginRequest = {
    scopes: []
};

/**
 * Add here the endpoints and scopes when obtaining an access token for protected web APIs. For more information, see:
 * https://github.com/AzureAD/microsoft-authentication-library-for-js/blob/dev/lib/msal-browser/docs/resources-and-scopes.md
 */
export const protectedResources = {
    scopes: [process.env.REACT_APP_Scopes],
    apiUserProjectHolder: {
        endpoint: SERVER_HOST + "/api/user/projectholder",
    },
    apiGetProjectHolder: {
        endpoint: SERVER_HOST + "/api/get/projectholder",
    },
    apiGetProjectsFromHolderUser: {
        endpoint: SERVER_HOST + "/api/projects/user/projectholder",
    },
    apiGetUserWithEmail: {
        endpoint: SERVER_HOST + "/api/get/oid",
    },
    apiUserInfo: {
        endpoint: SERVER_HOST + "/api/get/UserInfo",
    },
    apiGetUserWithId: {
        endpoint: SERVER_HOST + "/api/get/user",
    },
    apiGetAllUsers: {
        endpoint: SERVER_HOST + "/api/list/user",
    },
    apiDeleteUser: {
        endpoint: SERVER_HOST + "/api/delete/user",
    },
    apiCreateContainer: {
        endpoint: SERVER_HOST + "/api/create/container",
    },
    apiDeleteContainer: {
        endpoint: SERVER_HOST + "/api/delete/container",
    },
    apiGetContainer: {
        endpoint: SERVER_HOST + "/api/get/container",
    },
    apiGetProjects: {
        endpoint: SERVER_HOST + "/api/list/project",
    },
    apiGetProject: {
        endpoint: SERVER_HOST + "/api/get/project",
    },
    apiProjectContainers: {
        endpoint: SERVER_HOST + "/api/containers/project",
    },
    apiCreateProject: {
        endpoint: SERVER_HOST + "/api/create/project",
    },
    apiUpdateProject: {
        endpoint: SERVER_HOST + "/api/update/project",
    },
    apiDeleteProject: {
        endpoint: SERVER_HOST + "/api/delete/project",
    },
    apiUpdateProjectUser: {
        endpoint: SERVER_HOST + "/api/reassign/project",
    },
    apiGetVideo: {
        endpoint: SERVER_HOST + "/api/get/blob",
    },
    apiGetClasses: {
        endpoint: SERVER_HOST + "/api/get/classes",
    },
    apiGetCsv: {
        endpoint: SERVER_HOST + "/api/records/csv",
    },
    apiGetPersistentCsv: {
        endpoint: SERVER_HOST + "/api/records/persistentCsv",
    },
    apiSaveCsv: {
        endpoint: SERVER_HOST + "/api/save/csv",
    },
    apiSubmit: {
        endpoint: SERVER_HOST + "/api/create/submission",
    },
    apiDeleteRecords: {
        endpoint: SERVER_HOST + "/api/delete/records",
    },
    apiListBlobs: {
        endpoint: SERVER_HOST + "/api/get/blobList",
    }
}