import Button from '@material-ui/core/Button';
import Tooltip from '@material-ui/core/Tooltip';
import { withStyles } from '@material-ui/core/styles';
import { Link } from "react-router-dom"

/**
 * Create a component to enable navigation for admin pages.
 * @returns Component that enables the admin to access admin pages
 */
const AdminPage = () => {

    //This creates a custom tooltip with the given styles.
    const CstmTooltip = withStyles((theme) => ({
        tooltip: {
            backgroundColor: '#f5f5f9',
            color: 'rgba(0, 0, 0, 1)',
            maxWidth: 500,
            fontSize: theme.typography.pxToRem(12),
            border: '1px solid #dadde9',
        },
    }))(Tooltip);

    return (
        <div className="admin-panel">
            <h1><center>Admin Panel</center></h1>
            <br /><br /><br /><br />

            {/*This creates a button that displays a tooltip when hovered.*/}
            <CstmTooltip title="This is where you can create, assign and edit projects and their containers.">
                <Link to={"/projects"}>
                    <Button variant="contained" style={{
                        maxWidth: '300px', maxHeight: '300px',
                        minWidth: '300px', minHeight: '300px', margin: '30px'
                    }}>
                        All Projects
                    </Button>
                </Link>
            </CstmTooltip>

            {/*This creates a button that displays a tooltip when hovered.*/}
            <CstmTooltip title="This is where you can view all the registered users and their projects.">
                <Link to={"/users"}>
                    <Button variant="contained" style={{
                        maxWidth: '300px', maxHeight: '300px',
                        minWidth: '300px', minHeight: '300px', margin: '30px'
                    }}>
                        All Users
                    </Button>
                </Link>
            </CstmTooltip>

        </div>
    );
}

export default AdminPage;