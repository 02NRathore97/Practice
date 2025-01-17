package com.rays.ctl;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rays.common.BaseCtl;
import com.rays.common.DropDownList;
import com.rays.common.ORSResponse;
import com.rays.dto.AttachmentDTO;
import com.rays.dto.UserDTO;
import com.rays.form.UserForm;
import com.rays.service.AttachmentServiceInt;
import com.rays.service.UserServiceInt;

@RestController
@RequestMapping(value = "User")
public class UserCtl extends BaseCtl {

	@Autowired
	public UserServiceInt userService;

	@Autowired
	public AttachmentServiceInt attachmentServiceInt;

	@GetMapping("preload")
	public ORSResponse preload() {

		ORSResponse res = new ORSResponse();
		UserDTO dto = new UserDTO();
		List<DropDownList> userList = userService.search(dto, 0, 0);
		res.addResult("userList", userList);
		return res;

	}

	@PostMapping("save")
	public ORSResponse save(@RequestBody @Valid UserForm form, BindingResult bindingResult) {

		ORSResponse res = validate(bindingResult);

		if (!res.isSuccess()) {
			return res;
		}

		UserDTO dto = (UserDTO) form.getDto();
		if (dto.getId() != null && dto.getId() > 0) {
			userService.update(dto);
			res.addData(dto.getId());
			res.addMessage("Data Updated Successfully !!!");
		} else {
			long pk = userService.add(dto);
			res.addData(pk);
			res.addMessage("Data added successfully !!!");
		}

		return res;
	}

	@GetMapping("get/{id}")
	public ORSResponse get(@PathVariable long id) {
		ORSResponse res = new ORSResponse();
		UserDTO dto = userService.findById(id);
		res.addData(dto);
		return res;

	}

	@GetMapping("delete/{ids}")
	public ORSResponse delete(@PathVariable long[] ids) {
		ORSResponse res = new ORSResponse();

		for (long id : ids) {
			userService.delete(id);
		}

		res.addMessage("Data Deleted Successfully !!!");
		return res;
	}

	@PostMapping("search")
	public ORSResponse search(@RequestBody UserForm form) {
		ORSResponse res = new ORSResponse();

		UserDTO dto = (UserDTO) form.getDto();
		List list = userService.search(dto, 0, 5);

		if (list.size() == 0) {
			res.addMessage("Record not found !!!");
		} else {
			res.addData(list);

		}

		return res;
	}

	@PostMapping("/profilePic/{userId}")
	public ORSResponse uploadPic(@PathVariable Long userId, @RequestParam("file") MultipartFile file,
			HttpServletRequest req) {
		ORSResponse res = new ORSResponse();
		AttachmentDTO attachmentDTO = new AttachmentDTO();
		attachmentDTO.setDescription("profile Pic");
		attachmentDTO.setUserId(userId);

		UserDTO userDto = userService.findById(userId);

		if (userDto.getId() != null && userDto.getId() > 0) {
			attachmentDTO.setId(userDto.getImageId());
		}

		long imageId = attachmentServiceInt.save(attachmentDTO);

		if (userDto.getImageId() == null) {

			userDto.setImageId(imageId);

			userService.update(userDto);
		}
		res.addResult("imageId", imageId);

		return res;
	}

	@GetMapping("/profilePic/{userId}")
	public @ResponseBody void downloadPic(@PathVariable Long userId, HttpServletResponse response) {

		try {

			UserDTO userDto = userService.findById(userId);

			AttachmentDTO attachmentDTO = null;

			if (userDto != null) {
				attachmentDTO = attachmentServiceInt.findById(userDto.getImageId());
			}

			if (attachmentDTO != null) {
				response.setContentType(attachmentDTO.getType());
				OutputStream out = response.getOutputStream();
				out.write(attachmentDTO.getDoc());
				out.close();
			} else {
				response.getWriter().write("ERROR: File not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
