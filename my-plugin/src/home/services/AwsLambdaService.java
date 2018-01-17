package home.services;

import com.intellij.openapi.components.ServiceManager;

public interface AwsLambdaService {

    static AwsLambdaService getInstance() {
        return ServiceManager.getService(AwsLambdaService.class);
    }
}
