package com.dave.beeskium.config;

import com.dave.beeskium.model.Service;
import com.dave.beeskium.model.Staff;
import com.dave.beeskium.model.Barbershop;
import com.dave.beeskium.repository.BarbershopRepository;
import com.dave.beeskium.repository.ServiceRepository;
import com.dave.beeskium.repository.StaffRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
//NOTA: Classe utillizzata solamente in fase di testing, da rimuovere in produzione
@Component
public class DataInitializer implements CommandLineRunner {

    private final BarbershopRepository barbershopRepository;
    private final StaffRepository staffRepository;
    private final ServiceRepository serviceRepository;

    public DataInitializer(BarbershopRepository barbershopRepository,
            StaffRepository staffRepository,
            ServiceRepository serviceRepository) {
        this.barbershopRepository = barbershopRepository;
        this.staffRepository = staffRepository;
        this.serviceRepository = serviceRepository;
    }

    @Override
    public void run(String... args) {
        Barbershop barbershop = barbershopRepository.findBySlug("beeskium").orElseGet(() -> {
            Barbershop created = new Barbershop();
            created.setSlug("beeskium");
            created.setName("Beeskium Barberia");
            created.setAddress("Via Senatore Lo Schiavo 38, Taurianova RC");
            created.setMapsUrl("https://maps.app.goo.gl/vybsgksxom7sRaPb6?g_st=ic");
            created.setWhatsapp("https://wa.me/3909661820267");
            created.setInstagram("https://www.instagram.com/beeskium.barber/");
            created.setPhone("tel:09661820267");
            return barbershopRepository.save(created);
        });

        if (staffRepository.findByBarbershop_Slug("beeskium").isEmpty()) {
            Staff staff = new Staff();
            staff.setFirstName("Giovanni");
            staff.setLastName("Rossi");
            staff.setEmail("giovanni@beeskium.it");
            staff.setPhone("09661820267");
            staff.setIsActive(true);
            staff.setBarbershop(barbershop);
            staffRepository.save(staff);
        }

        if (serviceRepository.findByBarbershop_Slug("beeskium").isEmpty()) {
            serviceRepository.save(createService("Taglio capelli", "Taglio classico", "15.00", 30, barbershop));
            serviceRepository.save(createService("Barba", "Rasatura e rifinitura barba", "10.00", 20, barbershop));
            serviceRepository.save(createService("Taglio + Barba", "Pacchetto completo", "22.00", 45, barbershop));
        }
    }

    private Service createService(String name, String description, String price, int durationMinutes, Barbershop barbershop) {
        Service service = new Service();
        service.setName(name);
        service.setDescription(description);
        service.setPrice(new BigDecimal(price));
        service.setDurationMinutes(durationMinutes);
        service.setBarbershop(barbershop);
        return service;
    }
}
