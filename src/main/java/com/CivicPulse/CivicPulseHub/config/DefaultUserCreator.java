package com.CivicPulse.CivicPulseHub.config;


import com.CivicPulse.CivicPulseHub.entity.Department;
import com.CivicPulse.CivicPulseHub.entity.Officer;
import com.CivicPulse.CivicPulseHub.entity.User;
import com.CivicPulse.CivicPulseHub.repository.UserRepository;
import com.CivicPulse.CivicPulseHub.repository.DepartmentRepository;
import com.CivicPulse.CivicPulseHub.repository.OfficerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@RequiredArgsConstructor
public class DefaultUserCreator implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final OfficerRepository officerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        createAdmin();

        createOfficer("rajivsharma", "rajivsharmacivicpulse@gmail.com",
                "OFFICER001", "WaterDepartment");

        createOfficer("vikramreddy", "vikramreddycivicpulse@gmail.com",
                "OFFICER002", "RoadDepartment");

        createOfficer("amitpatel", "amitpatelcivicpulse@gmail.com",
                "OFFICER003", "SanitationDepartment");

        createOfficer("sureshnair", "sureshnaircivicpulse@gmail.com",
                "OFFICER004", "ElectricityDepartment");

        createOfficer("arjunsingh", "arjunsinghcivicpulse@gmail.com",
                "OFFICER005", "Parks&TreesDepartment");

        createOfficer("manojkumar", "manojkumarcivicpulse@gmail.com",
                "OFFICER006", "UrbanPlanningDepartment");

        createOfficer("deepakverma", "deepakvermacivicpulse@gmail.com",
                "OFFICER007", "AnimalControlDepartment");

        createOfficer("rakeshyadav", "rakeshyadavcivicpulse@gmail.com",
                "OFFICER008", "PoliceDepartment");

        createOfficer("sanjaygupta", "sanjayguptacivicpulse@gmail.com",
                "OFFICER009", "DisasterManagementDepartment");
    }

    private void createAdmin() {

        if (userRepository.findByUsername("narendramodi").isEmpty()) {

            User admin = new User();
            admin.setUsername("narendramodi");
            admin.setEmail("narendracivicpulse@gmail.com");
            admin.setGovernmentId("ADMIN001");
            admin.setRole("ADMIN");
            admin.setEnabled(true);
            admin.setPassword(passwordEncoder.encode("Admin@123"));

            userRepository.save(admin);
        }
    }

    private void createOfficer(String username,
                               String email,
                               String govId,
                               String departmentName) {

        if (userRepository.findByUsername(username).isEmpty()) {

            // Create user
            User officerUser = new User();
            officerUser.setUsername(username);
            officerUser.setEmail(email);
            officerUser.setGovernmentId(govId);
            officerUser.setRole("OFFICER");
            officerUser.setEnabled(true);
            officerUser.setPassword(passwordEncoder.encode("Officer@123"));

            userRepository.save(officerUser);

            // Get department
            Department department = departmentRepository
                    .findByName(departmentName)
                    .orElseGet(() -> {
                        Department newDept = new Department();
                        newDept.setName(departmentName);
                        return departmentRepository.save(newDept);
                    });
            // Link officer to department
            Officer officer = new Officer();
            officer.setUser(officerUser);
            officer.setDepartment(department);

            officerRepository.save(officer);
        }
    }
}
