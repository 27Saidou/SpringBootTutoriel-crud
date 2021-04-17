package net.opendevup.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import net.opendevup.dao.EtudiantRepository;
import net.opendevup.enties.Etudiant;

@Controller
@RequestMapping(value = "/Etudiant")
public class EtudiantController {
	@Autowired
	private EtudiantRepository etudiantRepository;

	@Value("${dir.images}")
	private String imageDir;

	@GetMapping(value = "/Index")
	public String Index(Model model, @RequestParam(name = "page", defaultValue = "0") int p,
			@RequestParam(name = "motCle", defaultValue = "") String mc) {
		Page<Etudiant> pageEtudiant = etudiantRepository.chercherEtudiants("%" + mc + "%", PageRequest.of(p, 5));
		int pagesCount = pageEtudiant.getTotalPages();
		int pages[] = new int[pagesCount];
		for (int i = 0; i < pagesCount; i++)
			pages[i] = i;
		model.addAttribute("pages", pages);
		model.addAttribute("pageEtudiants", pageEtudiant);
		model.addAttribute("pageCourante", p);
		model.addAttribute("motCle", mc);
		return "etudiants";
	}

	@GetMapping(value = "/form")
	public String formEtudiant(Model model) {
		model.addAttribute("etudiant", new Etudiant());
		return "FormEtudiant";
	}

	@RequestMapping(value = "/SaveEtudiant", method = RequestMethod.POST)
	public String save(@Valid Etudiant et, BindingResult bindingResult,
			@RequestParam(name = "picture") MultipartFile file) throws Exception, IOException {

		if (bindingResult.hasErrors()) {
			return "FormEtudiant";
		}
		if (!(file.isEmpty())) {
			et.setPhoto(file.getOriginalFilename());
		}
		etudiantRepository.save(et);
		if (!(file.isEmpty())) {
			et.setPhoto(file.getOriginalFilename());
			file.transferTo(new File(imageDir + et.getId()));
		}

		return "redirect:Index";
	}
	@RequestMapping(value = "/getphoto",produces = MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody()
	public byte[]getPhoto(Long id) throws Exception{
		File f=new File(imageDir+id);
		return IOUtils.toByteArray(new FileInputStream(f));
		
	}
	@RequestMapping(value = "/supprimer")
	public String supprimer(Long id) {
		etudiantRepository.deleteById(id);
		return "redirect:Index";
	}
	@RequestMapping(value = "/edit")
	public String edit(Long id,Model model) {
		Etudiant et=etudiantRepository.getOne(id);
		model.addAttribute("etudiant",et);
		return "EditEtudiant";
	}

	@RequestMapping(value = "/UpdateEtudiant", method = RequestMethod.POST)
	public String update(@Valid Etudiant et, BindingResult bindingResult,
			@RequestParam(name = "picture") MultipartFile file) throws Exception, IOException {

		if (bindingResult.hasErrors()) {
			return "EditEtudiant";
		}
		if (!(file.isEmpty())) {
			et.setPhoto(file.getOriginalFilename());
		}
		etudiantRepository.save(et);
		if (!(file.isEmpty())) {
			et.setPhoto(file.getOriginalFilename());
			file.transferTo(new File(imageDir + et.getId()));
		}

		return "redirect:Index";
	}
}
