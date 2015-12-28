package com.lsp;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

/**
 * Created by lsp on 14.10.15.
 */
public class LDAPManager {

    private DirContext ctx;
    private SearchControls ctrls;
    public LDAPManager() throws NamingException {
        Properties env = new Properties();
        env.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.setProperty(Context.PROVIDER_URL, "ldap://localhost");
        env.setProperty(Context.SECURITY_PRINCIPAL, "CN=test,OU=Domain Service Accounts,DC=tumos,DC=lan");
        env.setProperty(Context.SECURITY_CREDENTIALS, "testpass");
        ctx = new InitialDirContext(env);
        ctrls = new SearchControls();
        ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);
    }

    public void testGetADRecords() throws NamingException {

        String filter =
                "(&(objectCategory=person)(objectClass=user)(sAMAccountName=*))"; //условие поиска

        String base = "DC=tumos, DC=lan"; //где искать
        //String[] ret_atts = {"givenName", "sn",
        //        "userAccountControl", "description",
        //       "sAMAccountName", "cn", "distinguishedName"}; // аттрибуты которые вернуться в поиске.

        //String[] att_values = new String[ret_atts.length];
        //ctrls.setReturningAttributes(ret_atts);
        NamingEnumeration answers = ctx.search(base, filter, ctrls);

        while (answers.hasMoreElements()) {
            SearchResult res = (SearchResult) answers.next();

            Attributes attr = res.getAttributes(); //Attributes collection for user returned by AD
            System.out.println(" attr=" + attr.get("name").toString());

        }
    }
    public Set<String> getComputersAfterDate() throws NamingException {
        Set<String> set=new HashSet<String>();
        String filter =
                "(&(objectCategory=computer)(cn=*))";//условие поиска
        String base = "DC=tumos, DC=lan"; //где искать
        NamingEnumeration answers = ctx.search(base, filter, ctrls);
        long date=new Date().getTime();
        date-=15552000000L; //-6 mounth
        while (answers.hasMoreElements()) {
            SearchResult res = (SearchResult) answers.next();
            Attributes attr = res.getAttributes(); //Attributes collection for user returned by AD
            if (attr.get("lastlogon") != null) {
                long javaTime=Long.parseLong(attr.get("lastlogon").get().toString());
                javaTime-=0x19db1ded53e8000L;
                javaTime/=10000;
                if(javaTime>date) set.add(attr.get("cn").get().toString());
            }
        }
        return set;
    }
    public Map<String,String[]> getLoginFullNameFromAD() throws NamingException {
        Map<String,String[]> map=new HashMap<String, String[]>();
        String filter =
                "(&(objectCategory=person)(objectClass=user))";//условие поиска
                //"(objectCategory=organizationalUnit)";
        String base = "DC=tumos, DC=lan"; //где искать
        NamingEnumeration answers = ctx.search(base, filter, ctrls);
        long date=new Date().getTime();
        date-=15552000000L; //-6 mounth
        String dn;
        while (answers.hasMoreElements()) {
            SearchResult res = (SearchResult) answers.next();
            Attributes attr = res.getAttributes(); //Attributes collection for user returned by AD
            dn=attr.get("distinguishedName").get().toString();
            if(dn.contains("OU=Отделы")&attr.get("lastlogon") != null){
                long javaTime=Long.parseLong(attr.get("lastLogon").get().toString());
                javaTime-=0x19db1ded53e8000L;
                javaTime/=10000;
                if(javaTime>date) {
                    String temp = dn.replaceAll(",OU=Отделы,DC=tumos,DC=lan", "");
                    dn = temp.replaceAll(".*,OU=", "");
                    String[] tmpAr={attr.get("name").get().toString(),dn};
                    map.put(attr.get("SamAccountName").get().toString(),tmpAr);
                }
            }
        }
        return map;
    }
}
