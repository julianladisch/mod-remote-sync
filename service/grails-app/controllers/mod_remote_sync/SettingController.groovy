package mod_remote_sync

import grails.rest.*
import grails.converters.*

import com.k_int.web.toolkit.settings.AppSetting

import com.k_int.okapi.OkapiTenantAwareController
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import org.olf.rs.workflow.*;

class SettingController extends OkapiTenantAwareController<AppSetting> {
  
  static responseFormats = ['json', 'xml']
  
  SettingController() {
    super(AppSetting)
  }


  def worker() {
    def result = [result:'OK']
    String tenant_header = request.getHeader('X-OKAPI-TENANT')
    log.debug("Worker thread invoked....${tenant_header}");
    // backgroundTaskService.performReshareTasks(tenant_header+'_mod_rs');
    render result as JSON
  }

}
