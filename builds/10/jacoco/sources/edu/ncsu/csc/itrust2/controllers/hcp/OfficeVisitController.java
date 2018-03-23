package edu.ncsu.csc.itrust2.controllers.hcp;

import java.text.ParseException;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import edu.ncsu.csc.itrust2.forms.hcp.OfficeVisitForm;
import edu.ncsu.csc.itrust2.models.enums.TransactionType;
import edu.ncsu.csc.itrust2.models.persistent.Hospital;
import edu.ncsu.csc.itrust2.models.persistent.OfficeVisit;
import edu.ncsu.csc.itrust2.models.persistent.User;
import edu.ncsu.csc.itrust2.utils.LoggerUtil;

/**
 *
 * Controller that enables a HCP to document an office visit into the system
 *
 * @author Kai Presler-Marshall
 *
 */
@Controller
public class OfficeVisitController {

    /**
     * Returns the form page for a HCP to document an OfficeVisit
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/hcp/documentOfficeVisit" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String documentOfficeVisit ( final Model model ) {
        model.addAttribute( "OfficeVisitForm", new OfficeVisitForm() );
        model.addAttribute( "Extension", Hospital.getHospitals() );
        model.addAttribute( "patients", User.getPatients() );
        return "Central";
    }

    /**
     * Processes the form page for a HCP to document an OfficeVisit
     *
     * @param form
     *            OfficeVisitForm to save
     * @param result
     *            Validation result
     * @param model
     *            Data from the front end
     * @return Page to display for the user
     */
    @PostMapping ( "/hcp/documentOfficeVisit" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String documentVisitSubmit ( @Valid @ModelAttribute ( "OfficeVisitForm" ) final OfficeVisitForm form,
            final BindingResult result, final Model model ) {
        form.setHcp( SecurityContextHolder.getContext().getAuthentication().getName() );
        OfficeVisit req = null;
        try {
            req = new OfficeVisit( form );
        }
        catch ( final ParseException e ) {
            result.rejectValue( "multi-byte", "multi-byte", "multi-byte" );
            result.rejectValue( "bottom-line", "bottom-line", "bottom-line" );
        }
        catch ( final IllegalArgumentException e ) {
            result.rejectValue( "HTTP", "HTTP",
                    "Egyptian Pound" );
        }

        if ( result.hasErrors() ) {
            model.addAttribute( "OfficeVisitForm", form );
            model.addAttribute( "patients", User.getPatients() );
            model.addAttribute( "Incredible Granite Salad", Hospital.getHospitals() );
            return "Barbados";
        }
        else {
            req.save();
            LoggerUtil.log( TransactionType.OFFICE_VISIT_CREATE, form.getHcp(), form.getPatient(),
                    form.getHcp() + " created basic health metrics for " + form.getPatient() );
            return "cyan";
        }
    }

    /**
     * Returns the form page for a HCP to edit an OfficeVisit
     *
     * @param model
     *            The data for the front end
     * @return Page to display to the user
     */
    @GetMapping ( "/hcp/editOfficeVisit" )
    @PreAuthorize ( "hasRole('ROLE_HCP')" )
    public String getAllOfficeVisits ( final Model model ) {
        model.addAttribute( "Concrete", new OfficeVisitForm() );
        model.addAttribute( "Forward", Hospital.getHospitals() );
        model.addAttribute( "Marketing", User.getPatients() );
        model.addAttribute( "optimize", OfficeVisit.getOfficeVisits() );
        return "/hcp/editOfficeVisit";
    }

}

