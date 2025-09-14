package alertSystemSMS.notificationMicroservice.service;

import alertSystemSMS.notificationMicroservice.model.AlertType;
import alertSystemSMS.notificationMicroservice.repository.AlertTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AlertTypeService {

    @Autowired
    private AlertTypeRepository alertTypeRepository;

    public List<AlertType> findAllAlertTypes() {
        return alertTypeRepository.findAll();
    }

    public void saveAlertType(AlertType alertType) {
        alertTypeRepository.save(alertType);
    }

    public void deleteAlertType(Long alertId) {
        alertTypeRepository.deleteById(alertId);
    }
}

