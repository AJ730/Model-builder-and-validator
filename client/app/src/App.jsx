import { AuthenticatedTemplate, UnauthenticatedTemplate, useAccount, useMsal } from "@azure/msal-react";
import { PageLayout } from "./components/core/PageLayout";
import "./App.css";
import { BrowserRouter, Route, Switch } from 'react-router-dom';
import { AllProjectsPage } from "./pages/AllProjectsPage";
import ProjectPage from './pages/ProjectPage'
import Homepage from "./pages/Homepage";
import AdminPage from "./pages/AdminPage";
import AllUsersPage from "./pages/AllUsersPage";
import ProjectHolderPage from "./pages/ProjectHolderPage";
import ContainerPage from "./pages/ContainerPage"

/**
 * The AuthenticatedTemplate and UnauthenticatedTemplate components determine whether or not a 
 * user is authenticated. If a user is authenticated then content of the page can be accessed 
 * depending on the path. 
 * 
 * Otherwise a message indicating a user is not authenticated is rendered.
 * 
 * @returns Component which represents the content of the page depending on whether the user 
 * is authenticated or not
 */
const MainContent = () => {

  const { accounts } = useMsal();
  const account = useAccount(accounts[0] || {});

  const isAdmin = (account && account.idTokenClaims.roles) ? account.idTokenClaims.roles[0] === "ADMIN" : false

  return (
    <div className="App">
      <div className="content">
        <AuthenticatedTemplate>
          <Switch>
            <Route path="/projects">
              {isAdmin ? <AllProjectsPage /> : <h1>You are not authorized as an admin.</h1>}
            </Route>

            <Route path="/container/:id">
              <ContainerPage />
            </Route>

            <Route path="/users">
              <AllUsersPage />
            </Route>

            <Route path="/project-holder/:id">
              <ProjectHolderPage />
            </Route>

            <Route path="/project/:id">
              <ProjectPage />
            </Route>

            <Route exact path="/">
              {isAdmin ? <AdminPage /> : <Homepage />}
            </Route>
          </Switch>
        </AuthenticatedTemplate>

        <UnauthenticatedTemplate>

          <h5 className="card-title">Please sign-in to see your profile information.</h5>

        </UnauthenticatedTemplate>
      </div>
    </div>
  );
};

/**
 * Combine the Pagelayout and MainContent components to create the App component
 * @returns A component which is encapsulated by a BrowserRouter for routing in the application
 */
export default function App() {
  return (
    <BrowserRouter>
      <PageLayout>
        <MainContent />
      </PageLayout>
    </BrowserRouter>
  );
}