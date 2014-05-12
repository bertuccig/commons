package sos.settings;

/**
 * Title: SOSConnectionSettings
 * Description: eine einfache Klasse zum Lesen einer settings-Tabelle
 * Copyright: Copyright (c) 2003
 * Company: SOS Berlin GmbH
 * @author <a href="mailto:ghassan.beydoun@sos-berlin.com">Ghassan Beydoun</a>
 * @resource sos.connection.jar sos.util.jar
 * @version 1.0
 * @author <a href="mailto:andreas.pueschel@sos-berlin.com">Andreas P�schel</a>
 * @resource sos.connection.jar sos.util.jar
 * @version 1.0
 */

import java.util.Properties;
import java.util.ArrayList;

import sos.connection.SOSConnection;
import sos.connection.SOSMSSQLConnection;
import sos.connection.SOSSybaseConnection;

import sos.util.SOSClassUtil;
import sos.util.SOSLogger;
import sos.util.SOSString;

public class SOSConnectionSettings extends sos.settings.SOSSettings {

    /** SOSConnection Objekt */
    private SOSConnection sosConnection;

    /** Name der Anwendung */
    protected String application = "";

    protected final static String APPLICATION = "APPLICATION";

    protected final static String SECTION = "SECTION";

    protected final static String NAME = "NAME";

    protected final static String VALUE = "VALUE";

    protected final static String TITLE = "TITLE";
    
    protected final static String LONG_VALUE = "LONG_VALUE";

    /** Name der Applikation f�r Z�hlervariablen */
    protected String entryCounterApplication = "counter";

    /** Name der Sektion f�r Z�hlervariablen */
    protected String entryCounterSection = "counter";

    /** Sortierfeld f�r Anzeige der Eintr�ge */
    protected String entryOrder = "NAME";

    protected String entrySettingTitle = "TITLE";

    /** Name der Sektion f�r Master Schemata */
    protected String entrySchemaSection = "**schema**";

    /** default file name bei long values binary */
    protected String defaultDocumentFileName = "settings_file.dat";

    /**
     * Konstruktor
     * 
     * @param sosConnection
     *            ein SOSConnection-Objekt
     * @param source
     *            Name der Tabelle f�r Einstellungen
     * @throws Exception
     * @see #SOSConnectionSettings( SOSConnection, String, String, String,
     *      SOSLogger )
     */
    public SOSConnectionSettings(SOSConnection sosConnection, String source,
            SOSLogger logger) throws Exception {

        super(source, logger);

        if (sosConnection == null)
                throw (new Exception(SOSClassUtil.getMethodName()
                        + " invalid sosConnection !!."));
        this.sosConnection = sosConnection;

    } // SOSConnectionSettings

    /**
     * @param sosConnection
     *            DBConnection
     * @param source
     *            Name der Tabelle
     * @param application
     *            Name der Anwendung
     * @param section
     *            Name der Sektion
     * @throws Exception
     * @see #SOSConnectionSettings( SOSConnection, String, SOSLogger )
     */
    public SOSConnectionSettings(SOSConnection sosConnection, String source,
            String application, SOSLogger logger) throws Exception {

        super(source, logger);
        if (sosConnection == null)
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": invalid sosConnection !!."));
        if (SOSString.isEmpty(application))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": invalid application name !!."));
        this.sosConnection = sosConnection;
        this.application = application;
    }

    /**
     * @param sosConnection
     *            DBConnection
     * @param source
     *            Name der Tabelle
     * @param application
     *            Name der Anwendung
     * @param section
     *            Name der Sektion
     * @throws Exception
     * @see #SOSConnectionSettings( SOSConnection, String, SOSLogger )
     */
    public SOSConnectionSettings(SOSConnection sosConnection, String source,
            String application, String section, SOSLogger logger)
            throws Exception {

        super(source, section, logger);
        if (sosConnection == null)
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": invalid sosConnection name."));
        if (SOSString.isEmpty(application))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": invalid application name."));
        this.sosConnection = sosConnection;
        this.application = application;
    }

    /**
     * Liefert alle Eintr�ge einer Sektion zur�ck.
     * 
     * @param String
     *            application Name der Anwendung
     * @param String
     *            section Name der Sektion
     * @return Properties Objekt, das alle Eintr�ge der Sektion darstellt.
     * @exception Exception
     * @see #getSection( String )
     */
    public Properties getSection(String application, String section)
            throws Exception {

        Properties entries = new Properties();
        StringBuffer query = null;

        if (SOSString.isEmpty(application))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": application has no value!"));

        if (SOSString.isEmpty(section))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": section has no value!"));

        logger.debug6("calling " + SOSClassUtil.getMethodName());

        if (this.ignoreCase) {
            query = new StringBuffer("SELECT \"").append(NAME).append("\",\"")
                    .append(VALUE).append("\" FROM ").append(source).append(
                            " WHERE %lcase(\"").append(APPLICATION).append(
                            "\") = '").append(application.toLowerCase())
                    .append("' AND %lcase(\"").append(SECTION)
                    .append("\") = '").append(section.toLowerCase()).append(
                            "' AND \"").append(SECTION).append("\" <> \"")
                    .append(NAME).append("\"");
        } else {
            query = new StringBuffer("SELECT \"").append(NAME).append("\",\"")
                    .append(VALUE).append("\" FROM ").append(source).append(
                            " WHERE \"").append(APPLICATION).append("\" = '")
                    .append(application).append("' AND \"").append(SECTION)
                    .append("\" = '").append(section).append("' AND \"")
                    .append(SECTION).append("\" <> \"").append(NAME).append(
                            "\"");
        }

        logger.debug9(".. query: " + query.toString());

        try {
            entries = sosConnection.getArrayAsProperties(query.toString());
        } catch (Exception e) {
            throw (new Exception(SOSClassUtil.getMethodName() + ":"
                    + e.toString()));
        }
        return entries;
    }

    /**
     * Liefert alle Eintr�ge einer Sektion zur�ck.
     * 
     * @param String
     *            section Name der Sektion
     * @return Properties Objekt, das alle Eintr�ge der Sektion darstellt.
     * @exception Exception
     * @see #getSection( String, String )
     */
    public Properties getSection(String section) throws Exception {

        logger.debug6("calling " + SOSClassUtil.getMethodName());

        if (SOSString.isEmpty(section))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": section has no value!"));

        if (this.application == null || this.application.length() == 0)
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": application has no value!"));

        return getSection(application, section);
    }

    /**
     * Liefert alle Eintr�ge einer Sektion zur�ck.
     * 
     * @return Properties Objekt, das alle Eintr�ge der Sektion darstellt.
     * @exception Exception
     * @see #getSection( String, String )
     */
    public Properties getSection() throws Exception {

        logger.debug6("calling " + SOSClassUtil.getMethodName());

        if (SOSString.isEmpty(application))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": application has no value!"));

        if (SOSString.isEmpty(section))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": section has no value!"));

        return getSection(application, section);
    }

    /**
     * Liefert alle Sektionen einer Anwendung zur�ck
     * 
     * @return ArrayList die alle Sektionen beinhaltet
     * @throws java.lang.Exception
     */
    public ArrayList getSections(String application) throws Exception {

        ArrayList sections = new ArrayList();
        StringBuffer query = null;

        if (SOSString.isEmpty(application))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": application has no value!"));

        logger.debug6("calling " + SOSClassUtil.getMethodName());

        if (this.ignoreCase) {
            query = new StringBuffer("SELECT \"").append(SECTION).append(
                    "\" FROM ").append(source).append(" WHERE %lcase(\"")
                    .append(APPLICATION).append("\") = '").append(
                            application.toLowerCase()).append("' AND \"")
                    .append(SECTION).append("\" <> \"").append(APPLICATION)
                    .append("\"");
        } else {
            query = new StringBuffer("SELECT \"").append(SECTION).append(
                    "\" FROM ").append(source).append(" WHERE \"").append(
                    APPLICATION).append("\" = '").append(application).append(
                    "' AND \"").append(SECTION).append("\" <> \"").append(
                    APPLICATION).append("\"");
        }

        logger.debug9(".. query: " + query.toString());
        try {
            sections = sosConnection.getArrayValue(query.toString());
        } catch (Exception e) {
            throw (new Exception(SOSClassUtil.getMethodName() + ": "
                    + e.toString()));
        }
        return sections;
    }

    public ArrayList getSections() throws Exception {

        if (SOSString.isEmpty(application))
                throw (new Exception(SOSClassUtil.getMethodName()
                        + ": application has no value!"));
        return this.getSections(application);
    }

    /**
     * Liefert den wert eines Eintrages zur�ck.
     * 
     * @param entry
     *            Name des Eintrages
     * @return String der Wert eines Eintrages
     * @throws java.lang.Exception
     * @see #getSectionEntry( String, String, String )
     */
    public String getSectionEntry(String entry) throws Exception {

        return this.getSectionEntry(application, section, entry);
    }

    /**
     * Liefert den wert eines Eintrages zur�ck.
     * 
     * @param String
     *            application Name der Anwendung
     * @param entry
     *            Name des Eintrages
     * @return String der Wert eines Eintrages
     * @throws java.lang.Exception
     * @see #getSectionEntry( String )
     */
    public String getSectionEntry(String application, String section,
            String entry) throws Exception {

        try {
            logger.debug6("calling " + SOSClassUtil.getMethodName());

            String query = this.getSectionEntryStatement(application,section,entry,VALUE);

            logger.debug9(".. query: " + query);
            
            return  sosConnection.getSingleValue(query);
            
        } catch (Exception e) {
            throw (new Exception(SOSClassUtil.getMethodName() + ": "
                    + e.toString()));
        }
    }


    /**
     * Liefert den Wert eines BLOB Eintrages (LONG_VALUE) als byte Array zur�ck.
     * 
     * @param entry
     *            Name des Eintrages
     * @return Inhalt als byte-array
     * @throws java.lang.Exception
     * @see #getSectionEntry( String, String, String )
     */
    public byte[] getSectionEntryDocument(String entry) throws Exception {

        return this.getSectionEntryDocument(application, section, entry);
    }


    /**
     * Liefert den Wert eines BLOB Eintrages (LONG_VALUE) als byte Array zur�ck.
     * 
     * @param 
     *            application Name der Anwendung
     * @param section 	Name der Sektion
     * @param entry
     *            Name des Eintrages
     * @return Inhalt als byte-array
     * @throws java.lang.Exception
     * @see #getSectionEntry( String )
     */
    public byte[] getSectionEntryDocument(String application, String section, String entry) throws Exception {
       
        try {
            logger.debug6("calling " + SOSClassUtil.getMethodName());

            String query = this.getSectionEntryStatement(application,section,entry,LONG_VALUE);
            
            logger.debug9(".. query: " + query);
            
            return sosConnection.getBlob(query);
            
            
        } catch (Exception e) {
            throw (new Exception(SOSClassUtil.getMethodName() + ": "
                    + e.toString()));
        }
    }

    /**
     * Schreibt den Inhalt eines Blob-Felds (LONG_VALUE) in eine Datei
     * 
     * @param entry
     *            Name des Eintrages
     * @return Anzahl geschriebener Bytes
     * @throws java.lang.Exception
     * @see #getSectionEntry( String, String, String )
     */
    public long getSectionEntryDocumentAsFile(String entry,String fileName) throws Exception {

        return this.getSectionEntryDocumentAsFile(application, section, entry,fileName);
    }


    /**
     * Schreibt den Inhalt eines Blob-Felds (LONG_VALUE) in eine Datei
     * 
     * @param 
     *            application Name der Anwendung
     * @param section 	Name der Sektion
     * @param entry
     *            Name des Eintrages
     * @return Anzahl geschriebener Bytes
     * @throws java.lang.Exception
     * @see #getSectionEntry( String )
     */
    public long getSectionEntryDocumentAsFile(String application, String section, String entry,String fileName) throws Exception {
       
        try {
            logger.debug6("calling " + SOSClassUtil.getMethodName());

            String query = this.getSectionEntryStatement(application,section,entry,LONG_VALUE);
            
            logger.debug9(".. query: " + query);
            
            return sosConnection.getBlob(query,fileName);
            
        } catch (Exception e) {
            throw (new Exception(SOSClassUtil.getMethodName() + ": "
                    + e.toString()));
        }
    }

    
    
    /**
     * Liefert SQL Statement f�r unterschiedlichen getSectionEntry
     * 
     * @param application  	Name der Applikation
     * @param section		Name der Sektion
     * @param entry			Name des Eintrages
     * @param field			DB Feldname des Eintages (zB VALUE oder LONG_VALUE )
     * @return
     * @throws Exception
     */
    private String getSectionEntryStatement(String application,String section,String entry, String field) throws Exception{
        StringBuffer query = null;
        
        try{
            if (this.ignoreCase) {
                query = new StringBuffer("SELECT \"").append(field).append(
                        "\" FROM ").append(source).append(" WHERE %lcase(\"")
                        .append(APPLICATION).append("\") = '").append(
                                application.toLowerCase()).append(
                                "' AND %lcase(\"").append(SECTION).append(
                                "\") = '").append(section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "'");
            } else {
                query = new StringBuffer("SELECT \"").append(field).append(
                        "\" FROM ").append(source).append(" WHERE \"").append(
                        APPLICATION).append("\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("'");
            }
            
            return query.toString();
        }
        catch (Exception e) {
            throw (new Exception(SOSClassUtil.getMethodName() + ": "
                    + e.toString()));
        }
    }

    /**
     * richtet einen Z�hler ein: existiert der Z�hler bereits so beh�lt er
     * seinen aktuellen Wert. Sonst wird der Z�hler neu eingerichtet.
     * 
     * @param String
     *            application Name der Anwendung
     * @param String
     *            section
     * @param String
     *            entry
     * @param String
     *            createdBy author
     * @return true bei Erfolg, sonst false
     * @throws java.lang.Exception
     */

    public boolean initSequence(String application, String section,
            String entry, String createdBy) throws Exception {
        boolean created = false;
        String counter = null;
        StringBuffer query = null;
        String initValue = "1"; // wird mit 1 gez�hlt
        int inserted = 0;

        try {

            logger.debug6("calling " + SOSClassUtil.getMethodName());

            try {
                if (this.ignoreCase) {
                    query = new StringBuffer("SELECT \"").append(VALUE).append(
                            "\" FROM ").append(source).append(
                            " WHERE %lcase(\"").append(APPLICATION).append(
                            "\") = '").append(application.toLowerCase())
                            .append("' AND %lcase(\"").append(SECTION).append(
                                    "\") = '").append(section.toLowerCase())
                            .append("' AND %lcase(\"").append(NAME).append(
                                    "\") = '").append(entry.toLowerCase())
                            .append("'");
                } else {
                    query = new StringBuffer("SELECT \"").append(VALUE).append(
                            "\" FROM ").append(source).append(" WHERE \"")
                            .append(APPLICATION).append("\" = '").append(
                                    application).append("' AND \"").append(
                                    SECTION).append("\" = '").append(section)
                            .append("' AND \"").append(NAME).append("\" = '")
                            .append(entry).append("'");
                }

                logger.debug9("query: " + query.toString());
                counter = sosConnection.getSingleValue(query.toString());
                logger.debug9(".. current counter value: " + counter);
            } catch (Exception e) {
                created = false;
                throw e;
            }

            if (counter != null && counter != "") return true; // Z�hler
                                                               // existiert

            try {

                query = new StringBuffer("INSERT INTO ").append(source).append(
                        "(\"").append(APPLICATION).append("\",\"").append(
                        SECTION).append("\",\"").append(NAME).append("\",\"")
                        .append(VALUE).append("\",\"").append("CREATED_BY")
                        .append("\",\"").append("MODIFIED_BY").append(
                                "\",\"CREATED\",\"MODIFIED\") VALUES('")
                        .append(application).append("','").append(section)
                        .append("','").append(entry).append("','").append(
                                initValue).append("','").append(createdBy)
                        .append("','").append(createdBy).append("',%now,%now")
                        .append(")");

                logger.debug9(".. query: " + query.toString());

                inserted = sosConnection.executeUpdate(query.toString());
                if (inserted > 0) created = true;
            } catch (Exception e) {
                created = false; // counter existiert schon
            }

        } catch (Exception e) {
            throw (new Exception(SOSClassUtil.getMethodName() + ": "
                    + e.toString()));
        }

        return created;

    }

    /**
     * Liefert nach Inkrementierung den Wert der eingegebenen Variable zur�ck.
     * 
     * @param application
     *            Name der Anwendung
     * @param section
     *            Name des Sektions
     * @param entry
     *            Name des Eintrags
     * @return int Wert der inkrementierten Variable bei Erfolg, sonst -1
     * 
     * @exception Exception
     *                wird ausgel�st falls ein Datenbankfehler vorliegt.
     */

    public int getLockedSequence(String application, String section,
            String entry) throws Exception {

        StringBuffer query = null;
        String updlock_from = "";
        String updlock_where = "";
        int sequence = -1;

        try {
            logger.debug6("calling " + SOSClassUtil.getMethodName());

            updlock_from = (this.sosConnection instanceof SOSMSSQLConnection || this.sosConnection instanceof SOSSybaseConnection) ? "%updlock"
                    : "";
            updlock_where = updlock_from.equals("") ? "%updlock" : "";

            boolean autoCommit = this.sosConnection.getAutoCommit();
            this.sosConnection.setAutoCommit(false);

            if (this.ignoreCase) {
                query = new StringBuffer("SELECT \"").append(VALUE).append(
                        "\" FROM ").append(source).append(" ").append(
                        updlock_from).append(" WHERE %lcase(\"").append(
                        APPLICATION).append("\") = '").append(
                        application.toLowerCase()).append("' AND %lcase(\"")
                        .append(SECTION).append("\") = '").append(
                                section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "' ").append(updlock_where);
            } else {
                query = new StringBuffer("SELECT \"").append(VALUE).append(
                        "\" FROM ").append(source).append(" ").append(
                        updlock_from).append(" WHERE \"").append(APPLICATION)
                        .append("\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("' ").append(
                                updlock_where);
            }

            logger.debug9(SOSClassUtil.getMethodName() + ": get result query: "
                    + query.toString());

            sequence = Integer.valueOf(
                    sosConnection.getSingleValue(query.toString())).intValue() + 1;

            if (this.ignoreCase) {
                query = new StringBuffer("UPDATE ").append(source).append(
                        " SET \"").append(VALUE).append("\"=%cast(%cast(\"").append(VALUE)
                        .append("\" integer)+1 varchar) WHERE %lcase(\"").append(APPLICATION)
                        .append("\") = '").append(application.toLowerCase())
                        .append("' AND %lcase(\"").append(SECTION).append(
                                "\") = '").append(section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "'");
            } else {
                query = new StringBuffer("UPDATE ").append(source).append(
                        " SET \"").append(VALUE).append("\"=%cast(%cast(\"").append(VALUE)
                        .append("\" integer)+1 varchar) WHERE \"").append(APPLICATION).append(
                                "\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("'");
            }

            logger.debug9(SOSClassUtil.getMethodName() + ": query: "
                    + query.toString());

            sosConnection.execute(query.toString());
            logger.debug9(SOSClassUtil.getMethodName()
                    + ": successfully executed: " + query.toString());

            this.sosConnection.setAutoCommit(autoCommit);

        } catch (Exception e) {
            logger.debug9(SOSClassUtil.getMethodName()
                    + ": an error occurred : " + e.toString());
            throw (e);
        }

        return sequence;
    }

    /**
     * Liefert nach Inkrementierung den Wert der eingegebenen Variable zur�ck.
     * 
     * @param application
     *            Name der Anwendung
     * @param section
     *            Name des Sektions
     * @param entry
     *            Name des Eintrags
     * @return String Wert der inkrementierten Variable bei Erfolg, sonst leere
     *         Zeichenkette
     * 
     * @exception Exception
     *                wird ausgel�st falls ein Datenbankfehler vorliegt.
     */

    public String getLockedSequenceAsString(String application, String section,
            String entry) throws Exception {

        StringBuffer query = null;
        String updlock_from = "";
        String updlock_where = "";
        String sequence = "";

        try {
            logger.debug6("calling " + SOSClassUtil.getMethodName());

            updlock_from = (this.sosConnection instanceof SOSMSSQLConnection || this.sosConnection instanceof SOSSybaseConnection) ? "%updlock"
                    : "";
            updlock_where = updlock_from.equals("") ? "%updlock" : "";

            boolean autoCommit = this.sosConnection.getAutoCommit();
            this.sosConnection.setAutoCommit(false);

            if (this.ignoreCase) {
                query = new StringBuffer("SELECT \"").append(VALUE).append(
                        "\" FROM ").append(source).append(" ").append(
                        updlock_from).append(" WHERE %lcase(\"").append(
                        APPLICATION).append("\") = '").append(
                        application.toLowerCase()).append("' AND %lcase(\"")
                        .append(SECTION).append("\") = '").append(
                                section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "' ").append(updlock_where);
            } else {
                query = new StringBuffer("SELECT \"").append(VALUE).append(
                        "\" FROM ").append(source).append(" ").append(
                        updlock_from).append(" WHERE \"").append(APPLICATION)
                        .append("\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("' ").append(
                                updlock_where);
            }

            logger.debug9(SOSClassUtil.getMethodName() + ": get result query: "
                    + query.toString());

            sequence = String.valueOf(Integer.parseInt(sosConnection
                    .getSingleValue(query.toString())) + 1);

            if (this.ignoreCase) {
                query = new StringBuffer("UPDATE ").append(source).append(
                        " SET \"").append(VALUE).append("\"=%cast(%cast(\"").append(VALUE)
                        .append("\" integer)+1 varchar) WHERE %lcase(\"").append(APPLICATION)
                        .append("\") = '").append(application.toLowerCase())
                        .append("' AND %lcase(\"").append(SECTION).append(
                                "\") = '").append(section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "'");
            } else {
                query = new StringBuffer("UPDATE ").append(source).append(
                        " SET \"").append(VALUE).append("\"=%cast(%cast(\"").append(VALUE)
                        .append("\" integer)+1 varchar) WHERE \"").append(APPLICATION).append(
                                "\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("'");
            }

            logger.debug9(SOSClassUtil.getMethodName() + ": query: "
                    + query.toString());

            sosConnection.execute(query.toString());
            logger.debug9(SOSClassUtil.getMethodName()
                    + ": successfully executed: " + query.toString());

            this.sosConnection.setAutoCommit(autoCommit);

        } catch (Exception e) {
            logger.debug9(SOSClassUtil.getMethodName()
                    + ": an error occurred : " + e.toString());
            throw (e);
        }
        return sequence;
    }

    /**
     * Liefert nach Inkrementierung den Wert der eingegebenen Variable zur�ck.
     * 
     * @param application
     *            Name der Anwendung
     * @param section
     *            Name des Sektions
     * @param entry
     *            Name des Eintrags
     * @return int Wert der inkrementierten Variable bei Erfolg, sonst -1
     * 
     * @exception Exception
     *                wird ausgel�st falls ein Datenbankfehler vorliegt.
     */

    public int getSequence(String application, String section, String entry)
            throws Exception {

        StringBuffer query = null;
        int sequence = -1;

        try {
            logger.debug6("calling " + SOSClassUtil.getMethodName());

            if (this.ignoreCase) {
                query = new StringBuffer("UPDATE ").append(source).append(
                        " SET \"").append(VALUE).append("\"=%cast(%cast(\"").append(VALUE)
                        .append("\" integer)+1 varchar) WHERE %lcase(\"").append(APPLICATION)
                        .append("\") = '").append(application.toLowerCase())
                        .append("' AND %lcase(\"").append(SECTION).append(
                                "\") = '").append(section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "'");
            } else {
                query = new StringBuffer("UPDATE ").append(source).append(
                        " SET \"").append(VALUE).append("\"=%cast(%cast(\"").append(VALUE)
                        .append("\" integer)+1 varchar) WHERE \"").append(APPLICATION).append(
                                "\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("'");
            }

            logger.debug9(SOSClassUtil.getMethodName() + ": query: "
                    + query.toString());

            sosConnection.execute(query.toString());
            logger.debug9(SOSClassUtil.getMethodName()
                    + ": successfully executed: " + query.toString());

            if (this.ignoreCase) {
                query = new StringBuffer("SELECT \"").append(VALUE).append(
                        "\" FROM ").append(source).append(" WHERE %lcase(\"")
                        .append(APPLICATION).append("\") = '").append(
                                application.toLowerCase()).append(
                                "' AND %lcase(\"").append(SECTION).append(
                                "\") = '").append(section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "'");
            } else {
                query = new StringBuffer("SELECT \"").append(VALUE).append(
                        "\" FROM ").append(source).append(" WHERE \"").append(
                        APPLICATION).append("\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("'");
            }

            logger.debug9(SOSClassUtil.getMethodName() + ": get result query: "
                    + query.toString());

            sequence = Integer.valueOf(
                    sosConnection.getSingleValue(query.toString())).intValue();

        } catch (Exception e) {
            logger.debug9(SOSClassUtil.getMethodName()
                    + ": an error occurred : " + e.toString());
            throw (e);
        }

        return sequence;
    }

    /**
     * Liefert nach Inkrementierung den Wert der eingegebenen Variable zur�ck.
     * 
     * @param application
     *            Name der Anwendung
     * @param section
     *            Name des Sektions
     * @param entry
     *            Name des Eintrags
     * @return String Wert der inkrementierten Variable bei Erfolg, sonst leere
     *         Zeichenkette
     * 
     * @exception Exception
     *                wird ausgel�st falls ein Datenbankfehler vorliegt.
     */

    public String getSequenceAsString(String application, String section,
            String entry) throws Exception {

        StringBuffer query = null;
        String sequence = "";
        try {
            logger.debug6("calling " + SOSClassUtil.getMethodName());

            if (this.ignoreCase) {
                query = new StringBuffer("UPDATE ").append(source).append(
                        " SET \"").append(VALUE).append("\"=%cast(%cast(\"").append(VALUE)
                        .append("\" integer)+1 varchar) WHERE %lcase(\"").append(APPLICATION)
                        .append("\") = '").append(application.toLowerCase())
                        .append("' AND %lcase(\"").append(SECTION).append(
                                "\") = '").append(section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "'");
            } else {
                query = new StringBuffer("UPDATE ").append(source).append(
                        " SET \"").append(VALUE).append("\"=%cast(%cast(\"").append(VALUE)
                        .append("\" integer)+1 varchar) WHERE \"").append(APPLICATION).append(
                                "\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("'");
            }

            logger.debug9(SOSClassUtil.getMethodName() + ": query: "
                    + query.toString());

            sosConnection.execute(query.toString());
            logger.debug9(SOSClassUtil.getMethodName()
                    + ": successfully executed: " + query.toString());

            if (this.ignoreCase) {
                query = new StringBuffer("SELECT \"").append(VALUE).append(
                        "\" FROM ").append(source).append(" WHERE %lcase(\"")
                        .append(APPLICATION).append("\") = '").append(
                                application.toLowerCase()).append(
                                "' AND %lcase(\"").append(SECTION).append(
                                "\") = '").append(section.toLowerCase())
                        .append("' AND %lcase(\"").append(NAME).append(
                                "\") = '").append(entry.toLowerCase()).append(
                                "'");
            } else {
                query = new StringBuffer("SELECT \"").append(VALUE).append(
                        "\" FROM ").append(source).append(" WHERE \"").append(
                        APPLICATION).append("\" = '").append(application)
                        .append("' AND \"").append(SECTION).append("\" = '")
                        .append(section).append("' AND \"").append(NAME)
                        .append("\" = '").append(entry).append("'");
            }

            logger.debug9(SOSClassUtil.getMethodName() + ": get result query: "
                    + query.toString());

            sequence = sosConnection.getSingleValue(query.toString());

        } catch (Exception e) {
            logger.debug9(SOSClassUtil.getMethodName()
                    + ": an error occurred : " + e.toString());
            throw (e);
        }
        return sequence;
    }

    /**
     * Aktiviert die Kleinschreibung f�r Feldnamen
     * 
     * @see #setKeysToUpperCase
     */
    public void setKeysToLowerCase() throws Exception {

        logger.debug6("calling " + SOSClassUtil.getMethodName());
        sosConnection.setKeysToLowerCase();
        logger.debug9(".. now keys set to lower case.");
    }

    /**
     * Aktiviert die Kleinschreibung f�r Feldnamen
     * 
     * @see #setKeysToLowerCase
     */
    public void setKeysToUpperCase() throws Exception {

        logger.debug6("calling " + SOSClassUtil.getMethodName());
        sosConnection.setKeysToUpperCase();
        logger.debug9(".. now keys set to upper case.");
    }

    /**
     * Setzt den Schalter f�r die Ber�cksichtigung von Gro�/Kleinschreibung
     * 
     * @param ignoreCase
     */
    public void setIgnoreCase(boolean ignoreCase) {

        this.ignoreCase = ignoreCase;
    }

    /**
     * liefert den Schalter f�r die Ber�cksichtigung von Gro�/Kleinschreibung
     * 
     * @param ignoreCase
     */
    public boolean getIgnoreCase() {

        return this.ignoreCase;
    }

    /**
     * Liefert den Name der Anwendung
     * 
     * @return Name der Anwendung.
     */
    public String getApplication() {
        return application;
    }

    /**
     * Setzt den Name der Anwendung
     * 
     * @param application   Name der Anwendung. Default : Leerstring
     */
    public void setApplication(String application) {
        this.application = application;
    }

    /**
     * Liefert den  Name der Applikation f�r Z�hlervariablen
     * 
     * @return  Name der Applikation f�r Z�hlervariablen.
     */
    public String getEntryCounterApplication() {
        return entryCounterApplication;
    }

    /**
     * Setzt den  Name der Applikation f�r Z�hlervariablen
     * 
     * @param entryCounterApplication
     *             Name der Applikation f�r Z�hlervariablen. Default : counter
     */
    public void setEntryCounterApplication(String entryCounterApplication) {
        this.entryCounterApplication = entryCounterApplication;
    }

    /**
     * Lifert den Name der Sektion f�r Z�hlervariablen
     * 
     * @return Name der Sektion f�r Z�hlervariablen.
     */
    public String getEntryCounterSection() {
        return entryCounterSection;
    }

    /**
     * Setzt den Name der Sektion f�r Z�hlervariablen
     * 
     * @param entryCounterSection
     *            Name der Sektion f�r Z�hlervariablen. Default : counter
     */
    public void setEntryCounterSection(String entryCounterSection) {
        this.entryCounterSection = entryCounterSection;
    }

    /**
     * Liefert Sortierfeld(er) f�r Anzeige der Eintr�ge
     * 
     * @return Sortierfeld(er) f�r Anzeige der Eintr�ge.
     */
    public String getEntryOrder() {
        return entryOrder;
    }

    /**
     * Setzt Sortierfeld(er) f�r Anzeige der Eintr�ge
     * 
     * @param entryOrder
     *            Sortierfeld(er) f�r Anzeige der Eintr�ge. Default : NAME
     */
    public void setEntryOrder(String entryOrder) {
        this.entryOrder = entryOrder;
    }

    /**
     * Liefert den Name der Sektion f�r Master Schemata
     * 
     * @return Name der Sektion f�r Master Schemata
     */
    public String getEntrySchemaSection() {
        return entrySchemaSection;
    }

    /**
     * Setzt den Name der Sektion f�r Master Schemata
     * 
     * @param entrySchemaSection
     *            Name der Sektion f�r Master Schemata. Default : **schema**
     */
    public void setEntrySchemaSection(String entrySchemaSection) {
        this.entrySchemaSection = entrySchemaSection;
    }

    /**
     * Liefert das Feld f�r Titels der Eintr�ge
     * 
     * @return Returns the entrySettingTitle.
     */
    public String getEntrySettingTitle() {
        return entrySettingTitle;
    }

    /**
     * Setzt das Feld f�r Titels der Eintr�ge
     * 
     * @param entrySettingTitle
     *            Feld f�r Titels der Eintr�ge.
     */
    public void setEntrySettingTitle(String entrySettingTitle) {
        this.entrySettingTitle = entrySettingTitle;
    }
    
    /**
     * Default Filename f�r LONG_VALUE
     * 
     * @return Returns the defaultDocumentFileName.
     */
    public String getDefaultDocumentFileName() {
        return defaultDocumentFileName;
    }
    
    /**
     * Default Filename f�r LONG_VALUE
     * 
     * @param fileName The fileName to set.
     */
    public void setDefaultDocumentFileName(String fileName) {
        this.defaultDocumentFileName = fileName;
    }
}
