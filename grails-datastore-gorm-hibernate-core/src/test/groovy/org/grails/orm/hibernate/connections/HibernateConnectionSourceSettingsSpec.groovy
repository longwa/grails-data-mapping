package org.grails.orm.hibernate.connections

import org.grails.datastore.mapping.core.DatastoreUtils
import org.hibernate.dialect.Oracle8iDialect
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.UrlResource
import spock.lang.Specification

/**
 * Created by graemerocher on 05/07/16.
 */
class HibernateConnectionSourceSettingsSpec extends Specification {

    void "test hibernate connection source settings"() {
        when:"The configuration is built"
        Map config = [
                'dataSource.dbCreate': 'update',
                'dataSource.dialect': Oracle8iDialect.name,
                'dataSource.formatSql': 'true',
                'hibernate.flush.mode': 'COMMIT',
                'hibernate.cache.queries': 'true',
                'hibernate.hbm2ddl.auto': 'create',
                'hibernate.configLocations':'file:hibernate.cfg.xml'
        ]
        HibernateConnectionSourceSettingsBuilder builder = new HibernateConnectionSourceSettingsBuilder(DatastoreUtils.createPropertyResolver(config))
        HibernateConnectionSourceSettings settings = builder.build()

        def expectedDataSourceProperties = new Properties()
        expectedDataSourceProperties.put('hibernate.hbm2ddl.auto', 'update')
        expectedDataSourceProperties.put('hibernate.show_sql', 'false')
        expectedDataSourceProperties.put('hibernate.format_sql', 'true')
        expectedDataSourceProperties.put('hibernate.dialect', Oracle8iDialect.name)

        def expectedHibernateProperties = new Properties()
        expectedHibernateProperties.put('hibernate.hbm2ddl.auto', 'create')
        expectedHibernateProperties.put('hibernate.cache.queries', 'true')
        expectedHibernateProperties.put('hibernate.flush.mode', 'COMMIT')
        expectedHibernateProperties.put('hibernate.naming_strategy','org.hibernate.cfg.ImprovedNamingStrategy')
        expectedHibernateProperties.put('hibernate.configLocations','file:hibernate.cfg.xml')

        def expectedCombinedProperties = new Properties()
        expectedCombinedProperties.putAll(expectedDataSourceProperties)
        expectedCombinedProperties.putAll(expectedHibernateProperties)

        then:"The results are correct"
        settings.dataSource.dbCreate == 'update'
        settings.dataSource.dialect == Oracle8iDialect
        settings.dataSource.formatSql
        !settings.dataSource.logSql
        settings.dataSource.toHibernateProperties() == expectedDataSourceProperties

        settings.hibernate.getFlush().mode == HibernateConnectionSourceSettings.HibernateSettings.FlushSettings.FlushMode.COMMIT
        settings.hibernate.getCache().queries
        settings.hibernate.get('hbm2ddl.auto') == 'create'
        settings.hibernate.getConfigLocations().size() == 1
        settings.hibernate.getConfigLocations()[0] instanceof UrlResource
        settings.hibernate.toProperties() == expectedHibernateProperties
    }
}
