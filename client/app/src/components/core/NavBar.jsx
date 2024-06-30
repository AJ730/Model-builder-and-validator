import { useMsal, useAccount } from "@azure/msal-react";
import { useState, useEffect } from "react";
import { EventType } from "@azure/msal-browser";
import { loginRequest } from "../../authConfig";
import { Button } from "react-bootstrap";
import { NavLink, Route, Switch } from 'react-router-dom';
import { protectedResources } from '../../authConfig';
import { callApiWithToken } from '../../util/useFetch'
import Navbar from "react-bootstrap/Navbar";
import Avatar from '@material-ui/core/Avatar';
import Popover from '@material-ui/core/Popover';
import ButtonM from '@material-ui/core/Button';

const SignInButton = () => {
    const { instance } = useMsal();

    const endpoint = protectedResources.apiUserInfo.endpoint;


    //Send user information to server on log-in
    useEffect(() => {
        // This will be run on component mount
        const callbackId = instance.addEventCallback((message) => {
            // This will be run every time an event is emitted after registering this callback
            if (message.eventType === EventType.LOGIN_SUCCESS) {
                const result = message.payload;
                callApiWithToken(result.idToken, endpoint, result.account, "json", "GET")
            }
        });

        return () => {
            // This will be run on component unmount
            if (callbackId) {
                instance.removeEventCallback(callbackId);
            }
        }

    }, []);

    return (
        <Button variant="secondary" onClick={() => instance.loginRedirect(loginRequest)} className="ml-auto">Sign In</Button>
    )
}

const SignOutButton = () => {
    const [anchorEl, setAnchorEl] = useState(null);
    const { instance, accounts } = useMsal();
    const account = useAccount(accounts[0] || {});

    return (
        <div style={{ position: 'absolute', right: '2%' }}>
            <Avatar onClick={(e) => setAnchorEl(e.currentTarget)}> {account.username.charAt(0)} </Avatar>
            <Popover PaperProps={{ style: { minWidth: '300px', minHeight: '200px' } }} open={Boolean(anchorEl)} anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
                transformOrigin={{ vertical: 'top', horizontal: 'center' }} anchorEl={anchorEl} onClose={() => setAnchorEl(null)}>
                <br></br>
                <Avatar style={{ width: '50px', height: '50px', margin: 'auto' }}> {account.name.charAt(0)} </Avatar>
                <h6 style={{ textAlign: 'center', margin: '10px' }}>Signed in as:</h6>
                <h6 style={{ textAlign: 'center', margin: '10px' }}>{account.username}</h6>
                <ButtonM variant="contained" style={{ position: 'absolute', bottom: '5%', right: '35%' }} onClick={() => instance.logoutRedirect({ postLogoutRedirectUri: "/" })}>
                    Sign Out
                </ButtonM>
            </Popover>
        </div>
    )
}

/**
 * 
 * @param {Object} props props with the isAuthenticated property which to determine whether the user is authenticated or not
 * @returns 
 */
const NavBar = ({ isAuthenticated }) => {
    const { accounts } = useMsal();
    const account = useAccount(accounts[0] || {});
    const isAdmin = (account && account.idTokenClaims.roles) ? account.idTokenClaims.roles[0] === "ADMIN" : false

    return (
        <>
            <Navbar style={{position:'fixed', width:'100%', top:'0%', zIndex:'100'}}>
                <Navbar.Brand href="/">Recycleye Model Checker</Navbar.Brand>

                {/* Displays different links depending on the page*/
                    isAuthenticated &&

                    <Switch>
                        <Route path="/projects">
                            <>
                                <NavLink as="button" to="/">Home</NavLink>
                            </>
                        </Route>

                        <Route path="/container/:id">
                            <>
                                <NavLink as="button" to="/">Home</NavLink>
                                {isAdmin ? null : <NavLink as="button" to={"/project-holder/" + btoa(account.idTokenClaims.oid)} >My Projects</NavLink>}
                            </>
                        </Route>

                        <Route path="/project-holder/:id">
                            <>
                                <NavLink as="button" to="/">Home</NavLink>
                            </>
                        </Route>

                        <Route path="/project/:id">
                            <>
                                <NavLink as="button" to="/">Home</NavLink>
                                {/* Encrypted oid usin btoa() */}
                                {isAdmin ? null : <NavLink as="button" to={"/project-holder/" + btoa(account.idTokenClaims.oid)} >My Projects</NavLink>}
                            </>
                        </Route>

                        <Route exact path="/users">
                            <>
                                <NavLink as="button" to="/">Home</NavLink>
                            </>
                        </Route>

                        <Route exact path="/">
                            <>
                                {isAdmin ? null : <NavLink as="button" to={"/project-holder/" + btoa(account.idTokenClaims.oid)} >My Projects</NavLink>}
                            </>
                        </Route>
                    </Switch>

                }

                {isAuthenticated ? <SignOutButton /> : <SignInButton />}

            </Navbar>
        </>

    );
}

export default NavBar;