# Open Sensor Search Service Module

## Configuration

See the main README.md for how to use the configuration files.

## Database backend configuration

OSS search distinguished between two databases: one for ``autocomplete`` functionality, which ideally should be very fast and must not contain all information, and one for ``full`` access to all data.
The connection is done via named Guice bindings in the database modules. The names for the autocomplete-search and full-search are ``ISearchSensorDAO.AUTOCOMPLETE`` respectively ``ISearchSensorDAO.FULL``.

In theory, a PostgreSQL database connection is implemeted for the ``full`` access, and a Solr database for the fast search for ``autocomplete``.

The following configuration uses the PostgreSQL database for both searches (see  ``PSQLModule.java``):

```
bind(ISearchSensorDAO.class).annotatedWith(Names.named(ISearchSensorDAO.FULL)).to(PGSQLSearchSensorDAO.class);
bind(ISearchSensorDAO.class).annotatedWith(Names.named(ISearchSensorDAO.AUTOCOMPLETE)).to(PGSQLSearchSensorDAO.class);
```

The following line in ``SolrModule.java`` activates Solr for autocomplete-search:

```
bind(ISearchSensorDAO.class).annotatedWith(Names.named(ISearchSensorDAO.AUTOCOMPLETE)).to(SOLRSearchSensorDAO.class);
```

Second, the used engines for search can be configured in the config file. The results are combined in a ``Set`` using the public sensor identifier, the full engine is used first (following the assumption that it contains more detailed information).

```
oss.search.useAutocompleteEngine=true
oss.search.useFullEngine=true
```