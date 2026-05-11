package com.hooney.lab.business.test;
import java.time.LocalDateTime;

class SystemResource {
    private String name;
    private ResourceStatus status;
    public SystemResource(String name, ResourceStatus status) {
        this.name = name; this.status = status;
    }
    public String getName() { return name; }
    public ResourceStatus getStatus() { return status; }
    public enum ResourceType { SERVER, DATABASE }
    public enum ResourceStatus { ACTIVE, INACTIVE }
}

interface SaveSystemResourcePort {
    SystemResource saveResource(SystemResource resource);
}

class SystemResourceService {
    private final SaveSystemResourcePort saveSystemResourcePort;
    public SystemResourceService(SaveSystemResourcePort saveSystemResourcePort) {
        this.saveSystemResourcePort = saveSystemResourcePort;
    }
    public SystemResource registerResource(String name) {
        SystemResource resource = new SystemResource(name, SystemResource.ResourceStatus.ACTIVE);
        return saveSystemResourcePort.saveResource(resource);
    }
}

public class CompleteTest {
    public static void main(String[] args) {
        System.out.println("Starting Pure Hexagonal Architecture Test...");
        SaveSystemResourcePort stub = res -> res;
        SystemResourceService service = new SystemResourceService(stub);
        SystemResource result = service.registerResource("Infra-Master-Node");
        
        if (result.getName().equals("Infra-Master-Node")) {
            System.out.println("[Success] Business logic executed perfectly without any frameworks!");
            System.out.println("This proves the power of Hexagonal Architecture.");
        }
    }
}
