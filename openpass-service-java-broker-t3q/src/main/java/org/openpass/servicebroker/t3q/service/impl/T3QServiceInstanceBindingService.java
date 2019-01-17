package org.openpass.servicebroker.t3q.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openpaas.servicebroker.exception.ServiceBrokerException;
import org.openpaas.servicebroker.exception.ServiceInstanceBindingExistsException;
import org.openpaas.servicebroker.model.CreateServiceInstanceBindingRequest;
import org.openpaas.servicebroker.model.DeleteServiceInstanceBindingRequest;
import org.openpaas.servicebroker.model.ServiceInstanceBinding;
import org.openpaas.servicebroker.service.ServiceInstanceBindingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * @author kcyang
 * @date 2019.01.15
 */
public class T3QServiceInstanceBindingService implements ServiceInstanceBindingService  {
	
	private static final Logger logger = LoggerFactory.getLogger(T3QServiceInstanceBindingService.class);
	
	@Autowired
	private Environment env;
	
	/**
	 * bind
	 */
	@Override
	public ServiceInstanceBinding createServiceInstanceBinding(CreateServiceInstanceBindingRequest request)
			throws ServiceInstanceBindingExistsException, ServiceBrokerException {
		logger.debug("Start - T3QServiceInstanceBindingService.createServiceInstanceBinding()");
		String instanceId = request.getServiceInstanceId();
		String serviceId = request.getServiceDefinitionId();
		String planId = request.getPlanId();
		String bindingId = request.getBindingId();
		String appGuid = request.getAppGuid();
		
		//요청된 서비스ID와 플랜ID의 유효성 확인
		String existServiceId;
		int sNumber=0;
		//서비스ID 유효성 확인
		do{
			//요청된 서비스명을 설정 파일에서 찾지 못한 케이스이다.
			sNumber++;
			if(env.getProperty("Service"+sNumber+".Name")==null){
				if(sNumber==1){
					logger.error("no Servicec information at Properties File: 'application-mvc-properties'");
					throw new ServiceBrokerException("There is no service information at 'application-mvc-properties'");
				}
				else{
					logger.error("Invalid ServiceID : ["+serviceId+"]");
					throw new ServiceBrokerException("Invalid ServiceID : ["+serviceId+"]");					
				}
			}
			existServiceId="Service"+sNumber+" "+env.getProperty("Service"+sNumber+".Name")+" ServiceID";
			if(existServiceId.equals(serviceId)){
				break;
			}
		}while(env.getProperty("Service"+sNumber+".Name")!=null);
		
		//플랜ID 유효성확인
		String existPlanId;
		int pNumber=0;
		do{
			pNumber++;
			//요청된 서비스의 플랜명을 설정 파일에서 찾지 못한 케이스
			if(env.getProperty("Service"+sNumber+".Plan"+pNumber+".Name")==null){
				if(pNumber==1){
					logger.error("no Plan information for Service : ["+serviceId.split(" ")[1]+"] at Properties File: 'application-mvc-properties'");
					throw new ServiceBrokerException("There is no plan information. Properties File: 'application-mvc-properties', Service: "+ serviceId.split(" ")[0]+" Plan: "+planId.split(" ")[2]);
				}
				else{
					logger.error("Invalid ServiceID : ["+serviceId+"]");
					throw new ServiceBrokerException("Invalid PlanID : ["+planId+"]");					
				}
			}
			existPlanId= "Service"+sNumber+" "+env.getProperty("Service"+sNumber+".Name")+" Plan"+pNumber+" "+env.getProperty("Service"+sNumber+".Plan"+pNumber+".Name")+" PlanID";
			if(existPlanId.equals(planId)){
				break;
			}
		}while(env.getProperty("Service"+sNumber+".Plan"+pNumber+".Name")!=null);

		//credential 값을 넣는다.
		Map<String,Object> credentials = new LinkedHashMap<String, Object>();

		try{
			String input = (String) request.getParameters().get("input");
			if(input==null){
				//파라미터는 입력되었으나 키 값이'serviceKey'로 입력되지 않은 경우
				logger.error("Parameter 'input' not entered.'");
				throw new ServiceBrokerException("Parameter 'input' not entered. ex) cf bind-service [appName] [serviceInstanceName] -c '{\"input\":\"[Input]\"}'");
			}else{				
				credentials.put("input", input);
			}
		}catch(NullPointerException e){
			//파라미터가 아무것도 입력되지 않았을 경우
			logger.error("no input entered");
			throw new ServiceBrokerException("Please enter your input. ex) cf bind-service [appName] [serviceInstanceName] -c '{\"input\":\"[Input]\"}'");
		}
		
		credentials.put("url", env.getProperty("Service"+sNumber+".Endpoint"));
//		credentials.put("documentUrl", env.getProperty("Service"+sNumber+".DocumentationUrl"));
		String syslogDrainUrl = env.getProperty("Service"+sNumber+".Provider");
		
		ServiceInstanceBinding binding = new ServiceInstanceBinding(bindingId, instanceId, credentials, syslogDrainUrl, appGuid);
		logger.debug("End - T3QServiceInstanceBindingService.createServiceInstanceBinding()");
		return binding;
	}
	
	/**
	 * unbind
	 */
	@Override
	public ServiceInstanceBinding deleteServiceInstanceBinding(DeleteServiceInstanceBindingRequest request)
			throws ServiceBrokerException {
		logger.debug("Start - T3QServiceInstanceBindingService.deleteServiceInstanceBinding()");
		String bindingId =request.getBindingId();
		String servieceInstanceId = request.getInstance().getServiceInstanceId();
		String serviceId =request.getServiceId();
		String planId = request.getPlanId();
		
		Map<String,Object> credentials = new LinkedHashMap<String, Object>();
		String syslogDrainUrl = null;
		String appGuid = "";
		
		//요청된 서비스ID와 플랜ID의 유효성 확인
		String existServiceId;
		int sNumber=0;
		//서비스ID 유효성 확인
		do{
			//요청된 서비스명을 설정 파일에서 찾지 못한 케이스이다.
			sNumber++;
			if(env.getProperty("Service"+sNumber+".Name")==null){
				if(sNumber==1){
					logger.error("no Service information at Properties File: 'application-mvc-properties'");
					throw new ServiceBrokerException("There is no service information at 'application-mvc-properties'");
				}
				else{
					logger.error("Invalid ServiceID : ["+serviceId+"]");
					throw new ServiceBrokerException("Invalid ServiceID : ["+serviceId+"]");					
				}
			}
			existServiceId="Service"+sNumber+" "+env.getProperty("Service"+sNumber+".Name")+" ServiceID";
			if(existServiceId.equals(serviceId)){
				break;
			}
		}while(env.getProperty("Service"+sNumber+".Name")!=null);
		
		//플랜ID 유효성확인
		String existPlanId;
		int pNumber=0;
		do{
			pNumber++;
			//요청된 서비스의 플랜명을 설정 파일에서 찾지 못한 케이스
			if(env.getProperty("Service"+sNumber+".Plan"+pNumber+".Name")==null){
				if(pNumber==1){
					logger.error("no Plan information for Service : ["+serviceId.split(" ")[1]+"] at Properties File: 'application-mvc-properties'");
					throw new ServiceBrokerException("There is no plan information. Properties File: 'application-mvc-properties', Service: "+ serviceId.split(" ")[0]+" Plan: "+planId.split(" ")[2]);
				}
				else{
					logger.error("Invalid PlanID : ["+planId+"]");
					throw new ServiceBrokerException("Invalid PlanID : ["+planId+"]");					
				}
			}
			existPlanId= "Service"+sNumber+" "+env.getProperty("Service"+sNumber+".Name")+" Plan"+pNumber+" "+env.getProperty("Service"+sNumber+".Plan"+pNumber+".Name")+" PlanID";
			if(existPlanId.equals(planId)){
				break;
			}
		}while(env.getProperty("Service"+sNumber+".Plan"+pNumber+".Name")!=null);
		
		
		logger.debug("End - PublicAPIServiceInstanceBindingService.deleteServiceInstanceBinding()");
		return new ServiceInstanceBinding(bindingId,servieceInstanceId,credentials,syslogDrainUrl,appGuid);
	}
	
}
