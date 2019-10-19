package com.iseplife.api.services;

import com.iseplife.api.dto.LDAPUserDTO;
import com.iseplife.api.dto.LDAPUserDTO;
import com.iseplife.api.exceptions.AuthException;
import com.iseplife.api.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.Hashtable;

@Service
public class LDAPService {
  private Logger LOG = LoggerFactory.getLogger(LDAPService.class);

  public LDAPUserDTO retrieveUser(String user, String mdp) {
    // Initial context implementation
    String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";
    String MY_HOST = "ldaps://ldap.isep.fr:636";
    String MGR_DN = "uid=" + user + ", " + "ou=People, dc=isep.fr";
    String MGR_PW = mdp;
    String MY_SEARCHBASE = "dc=isep.fr";
    String MY_FILTER = "(uid=" + user + ")";

    // Hashtable for environmental information
    Hashtable<String, String> env = new Hashtable<>();

    // Specify which class to use for our JNDI provider
    env.put(Context.INITIAL_CONTEXT_FACTORY, INITCTX);
    // Specify SSL
    // env.put(Context.SECURITY_PROTOCOL, "ssl"); // useless when using ldaps://
    // Specify host and port to use for directory service
    env.put(Context.PROVIDER_URL, MY_HOST);
    env.put(Context.SECURITY_AUTHENTICATION, "simple");
    env.put(Context.SECURITY_PRINCIPAL, MGR_DN);
    env.put(Context.SECURITY_CREDENTIALS, MGR_PW);
    env.put("com.sun.jndi.ldap.connect.timeout", "2000");
    env.put("com.sun.jndi.ldap.read.timeout", "2000");
    env.put("java.naming.ldap.version", "3");

    DirContext ctx = null;

    try {
      // Get a reference to a directory context
      ctx = new InitialDirContext(env);
    } catch (AuthenticationException e) {
      LOG.error("User not found");
      throw new AuthException("User not found", e);
    } catch (NamingException e) {
      LOG.error("Failed to connnect to the LDAP server");
      throw new AuthException("LDAP unavailable", e);
    }

    try {
      // Specify the scope of the search
      SearchControls constraints = new SearchControls();
      constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);

      // Perform the actual search
      // We give it a searchbase, a filter and a the constraints
      // containing the scope of the search
      NamingEnumeration<SearchResult> results = null;

      results = ctx.search(MY_SEARCHBASE, MY_FILTER, constraints);

      // Now step through the search results
      while (results != null && results.hasMore()) {
        SearchResult sr = results.next();

        Attributes attributes = sr.getAttributes();

        // for debugging purposes :
        NamingEnumeration<String> attributesIds = attributes.getIDs();
        while (attributesIds.hasMoreElements()) {
          String attribute = attributesIds.nextElement();
          LOG.debug("attribute: {}", attribute);
        }

        Attribute cn = attributes.get("cn");
        String nom = (String) cn.get();
        Attribute uid = attributes.get("uid");
        String login = (String) uid.get();
        Attribute et = attributes.get("employeeType");
        String type = (String) et.get();
        Attribute sn = attributes.get("sn");
        String nomFamille = (String) sn.get();
        Attribute givenName = attributes.get("givenname");
        String prenom = (String) givenName.get();
        Attribute en = attributes.get("employeeNumber");
        String employeeNumber = (String) en.get();
        Attribute em = attributes.get("mail");
        String mail = (String) em.get();

        ctx.close();

        LDAPUserDTO ldapUserDTO = new LDAPUserDTO();

        ldapUserDTO.setLogin(login);
        ldapUserDTO.setPassword("LDAP");
        ldapUserDTO.setFullName(nom);
        ldapUserDTO.setLastName(nomFamille);
        ldapUserDTO.setFirstName(prenom);
        ldapUserDTO.setEmployeeType(type);
        ldapUserDTO.setEmployeeNumber(employeeNumber);
        ldapUserDTO.setMail(mail);

        return ldapUserDTO;
      }

      LOG.error("Failed to retrieve {} from LDAP server", user);
      throw new NotFoundException("Could not retrieve user from LDAP");
    } catch (NamingException e) {
      LOG.error("LDAP unavailable");
      throw new AuthException("LDAP unavailable", e);
    }
  }
}
