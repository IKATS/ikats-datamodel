package fr.cs.ikats.workflow;

import java.util.List;

import fr.cs.ikats.common.dao.exception.IkatsDaoConflictException;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import fr.cs.ikats.common.dao.DataBaseDAO;
import fr.cs.ikats.common.dao.exception.IkatsDaoException;
import fr.cs.ikats.common.dao.exception.IkatsDaoMissingRessource;
import org.hibernate.exception.ConstraintViolationException;

/**
 * DAO class for MetaData model class
 */
public class WorkflowDAO extends DataBaseDAO {

    /**
     * HQL requests
     */
    private static final String GET_WORKFLOW_BY_NAME = "select wf from Workflow wf where wf.name = :name";
    private static final String GET_WORKFLOW_BY_ID = "select wf from Workflow wf where wf.id = :id";
    private static final String DELETE_WORKFLOW_BY_NAME = "delete wf from Workflow wf where wf.name = :name";
    private static final String DELETE_WORKFLOW_BY_ID = "delete wf from Workflow wf where wf.id = :id";

    /**
     * Logger for WorkflowDAO
     */
    private static final Logger LOGGER = Logger.getLogger(WorkflowDAO.class);

    /**
     * List all workflows
     *
     * @return the list of all workflows
     *
     * @throws IkatsDaoMissingRessource if there is no workflow
     * @throws IkatsDaoException if any other exception occurs
     */
    List<Workflow> listAll() throws IkatsDaoMissingRessource, IkatsDaoException {
        List<Workflow> result = null;
        Session session = getSession();

        try {
            Criteria criteria = session.createCriteria(Workflow.class);
            result = criteria.list();

        } catch (HibernateException hibException) {
            String msg = "Exception occurred while getting all workflow";
            LOGGER.error(msg);
            throw new IkatsDaoException(msg);
        } catch (Exception error) {
            if (error instanceof IkatsDaoMissingRessource) {
                throw error;
            } else {
                throw new IkatsDaoException();
            }
        } finally {
            session.close();
        }

        return result;
    }

    /**
     * Get a workflow by providing its name (which is unique)
     *
     * @param name name of the workflow to get
     *
     * @return the details of the workflow
     *
     * @throws IkatsDaoMissingRessource if there is no workflow matching the name
     * @throws IkatsDaoException if any other exception occurs
     */
    Workflow getByName(String name) throws IkatsDaoMissingRessource, IkatsDaoException {
        List<Workflow> result = null;
        Session session = getSession();

        try {

            Query q = session.createQuery(GET_WORKFLOW_BY_NAME);

            q.setString("name", name);
            result = q.list();
            if (result == null || (result.size() == 0)) {
                String msg = "Searching Workflow from name=" + name + ": no resource found, but should exist.";
                LOGGER.error(msg);

                throw new IkatsDaoMissingRessource(msg);
            }

        } catch (HibernateException hibException) {
            String msg = "Exception occurred while getting all workflow";
            LOGGER.error(msg);
            throw new IkatsDaoException(msg);
        } catch (Exception error) {
            if (error instanceof IkatsDaoMissingRessource) {
                throw error;
            } else {
                String msg = "Searching Workflow from name=" + name + ": unexpected Exception.";
                LOGGER.error(msg);
                throw new IkatsDaoException(msg);
            }
        } finally {
            session.close();
        }

        return result.get(0);
    }

    /**
     * Get a workflow by providing its id (which is unique)
     *
     * @param id
     *
     * @return
     *
     * @throws IkatsDaoMissingRessource if there is no workflow matching the id
     * @throws IkatsDaoException if any other exception occurs
     */
    Workflow getById(String id) throws IkatsDaoMissingRessource, IkatsDaoException {
        List<Workflow> result = null;
        Session session = getSession();

        try {
            Query q = session.createQuery(GET_WORKFLOW_BY_ID);
            q.setString("id", id);

            result = q.list();
            if (result == null || (result.size() == 0)) {
                String msg = "Searching workflow from id=" + id + ": no resource found, but should exist.";
                LOGGER.error(msg);

                throw new IkatsDaoMissingRessource(msg);
            }

        } catch (HibernateException hibException) {
            String msg = "Exception occurred while getting workflow id:" + id;
            LOGGER.error(msg);
            throw new IkatsDaoException(msg);
        } catch (Exception error) {
            if (error instanceof IkatsDaoMissingRessource) {
                throw error;
            } else {
                String msg = "Searching workflow from id=" + id + ": unexpected Exception.";
                LOGGER.error(msg);
                throw new IkatsDaoException(msg);
            }
        } finally {
            session.close();
        }

        return result.get(0);
    }

    /**
     * Save a workflow
     *
     * @param wf the workflow information to save
     *
     * @return the id of the created workflow
     *
     * @throws IkatsDaoConflictException if the workflow to append already exists
     * @throws IkatsDaoException if any other exception occurs
     */
    public Integer persist(Workflow wf) throws IkatsDaoConflictException, IkatsDaoException {
        Session session = getSession();
        Transaction tx = null;
        Integer wfId = null;
        String wfInfo = "null";
        try {
            wfInfo = wf.toString();
            tx = session.beginTransaction();
            wfId = (Integer) session.save(wf);
            tx.commit();
            LOGGER.debug("Created " + wfInfo + " with id=" + wf.getId());
        }
        catch (ConstraintViolationException e) {

            String msg = "Creating: " + wfInfo + ": already exists in base for same name";
            LOGGER.warn(msg);

            rollbackAndThrowException(tx, new IkatsDaoConflictException(msg, e));
        }
        catch (HibernateException e) {
            String msg = "Creating: " + wfInfo + ": unexpected HibernateException";
            LOGGER.error(msg, e);

            rollbackAndThrowException(tx, new IkatsDaoException(msg, e));
        }
        catch (Exception anotherError) {
            // Deals with null pointer exceptions ...
            String msg = "Creating Workflow: " + wfInfo + ": unexpected Exception";
            LOGGER.error(msg, anotherError);

            rollbackAndThrowException(tx, new IkatsDaoException(msg, anotherError));
        }
        finally {
            session.close();
        }
        return wfId;
    }

    /**
     * Update the workflow with the defined information
     *
     * @param wf the detailed information about the update
     *
     * @return true if the workflow update is successful
     *
     * @throws IkatsDaoConflictException if the workflow to update does not exist
     * @throws IkatsDaoException if any other exception occurs
     */
    public boolean update(Workflow wf) throws IkatsDaoConflictException, IkatsDaoException {
        Session session = getSession();
        Transaction tx = null;
        String wfInfo = "null";
        boolean updated = false;
        try {
            wfInfo = wf.toString();
            tx = session.beginTransaction();
            session.update(wf);
            tx.commit();
            updated = true;
            LOGGER.debug("Updated:" + wfInfo + " with value=" + wf.getRaw());
        }
        catch (ConstraintViolationException e) {

            String msg = "Updating: " + wfInfo + ": already exists in base for same name";
            LOGGER.warn(msg);

            rollbackAndThrowException(tx, new IkatsDaoConflictException(msg, e));
        }
        catch (HibernateException e) {
            String msg = "Updating: " + wfInfo + ": unexpected HibernateException";
            LOGGER.error(msg, e);

            rollbackAndThrowException(tx, new IkatsDaoException(msg, e));
        }
        catch (Exception anotherError) {
            // Deals with null pointer exceptions ...
            String msg = "Updating MetaData: " + wfInfo + ": unexpected Exception";
            LOGGER.error(msg, anotherError);

            rollbackAndThrowException(tx, new IkatsDaoException(msg, anotherError));
        }
        finally {
            session.close();
        }
        return updated;
    }

    /**
     * Delete a workflow identified by its name
     *
     * @param name name identifying the workflow to remove
     *
     * @return the id of the removed workflow
     *
     * @throws IkatsDaoException if the workflow couldn't be removed
     */
    public int removeByName(String name) throws IkatsDaoException {
        Session session = getSession();
        Transaction tx = null;
        int result = 0;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery(DELETE_WORKFLOW_BY_NAME);
            query.setString("name", name);
            result = query.executeUpdate();
            tx.commit();
        }
        catch (HibernateException e) {
            IkatsDaoException error = new IkatsDaoException("Deleting workflow rows matching name=" + name, e);
            LOGGER.error(error);
            rollbackAndThrowException(tx, error);
        }
        finally {
            session.close();
        }
        return result;
    }

    /**
     * Delete a workflow identified by its id
     *
     * @param id identifier of the workflow
     *
     * @return the id of the removed workflow
     * 
     * @throws IkatsDaoException if the workflow couldn't be removed
     */
    public int removeById(Integer id) throws IkatsDaoException {
        Session session = getSession();
        Transaction tx = null;
        int result = 0;
        try {
            tx = session.beginTransaction();
            Query query = session.createQuery(DELETE_WORKFLOW_BY_ID);
            query.setInteger("id", id);
            result = query.executeUpdate();
            tx.commit();
        }
        catch (HibernateException e) {
            IkatsDaoException error = new IkatsDaoException("Deleting Workflow rows matching id=" + id, e);
            LOGGER.error(error);
            rollbackAndThrowException(tx, error);
        }
        finally {
            session.close();
        }
        return result;
    }
}
