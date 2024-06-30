import React from "react";
import { useIsAuthenticated } from "@azure/msal-react";
import NavBar from "./NavBar";


/**
 * Renders the navbar component with a sign-in or sign-out button depending on whether or not a user is authenticated
 * @param props props
 */
export const PageLayout = (props) => {
    const isAuthenticated = useIsAuthenticated();

    return (
        <>
            <NavBar isAuthenticated={isAuthenticated}></NavBar>


            {props.children}
        </>
    );
};