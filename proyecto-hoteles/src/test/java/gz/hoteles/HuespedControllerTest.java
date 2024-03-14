package gz.hoteles;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import gz.hoteles.controller.HuespedController;
import gz.hoteles.repositories.HuespedRepository;

@ExtendWith(MockitoExtension.class)
public class HuespedControllerTest {
    @Mock
    HuespedRepository huespedRepository;

    @InjectMocks
    HuespedController huespedController;

    

}
