package com.zhua.pro.cms.controller.activiti;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.zhua.pro.cms.activiti.entity.ProDefined;
import com.zhua.pro.cms.activiti.util.ProcessKit;
import com.zhua.pro.cms.core.PageInfo;
import com.zhua.pro.cms.core.R;
import com.zhua.pro.cms.entity.*;
import com.zhua.pro.cms.util.StringUtils;
import com.zhua.pro.cms.activiti.vo.ActivitiModelVo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.repository.*;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * @ClassName ActivitiController
 * @Description TODO
 * @Author zhua
 * @Date 2020/11/29 20:54
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/activiti")
@AllArgsConstructor
public class ActivitiController {

    private IdentityService identityService;
    private RepositoryService repositoryService;

    private ObjectMapper objectMapper;


    /**
     * Describe: 流程视图
     * Param: modelAndView
     * Return: 流程视图
     * */
    @GetMapping({"/model/init","/model"})
    public ModelAndView modelInit(ModelAndView modelAndView){
        modelAndView.setViewName("page/admin/activiti/model/model-list");
        return modelAndView;
    }

    /**
     * Describe: 获取流程模型列表数据
     * Param: modelAndView
     * Return: R
     * */
    @GetMapping("model/page")
    public R modelPage(PageInfo<SysDict> pageInfo, ActivitiModelVo form){
        R r = new R();
        ModelQuery query = repositoryService.createModelQuery();
        if(StringUtils.isNotBlank(form.getCategory())) {
                query.modelCategoryLike(form.getCategory());
        }
        List<Model> list = query.orderByCreateTime().desc().listPage((pageInfo.getPage() - 1) * pageInfo.getLimit(), pageInfo.getLimit());
        long count = query.count();
        return r.successPage(count, list);
    }

    /**
     * Describe: 流程创建视图
     * Param: modelAndView
     * Return: 流程创建视图
     * */
    @GetMapping("/model/add")
    public ModelAndView modelAdd(ModelAndView modelAndView){
        modelAndView.setViewName("page/admin/activiti/model/model-add");
        return modelAndView;
    }

    /**
     * Describe: 创建流程图
     * Param: createModelParam
     * Return: Result
     * */
    @PostMapping("/model/create")
    public R modelCreate(@RequestBody ActivitiModelVo form) throws IOException {
        Model model = repositoryService.newModel();
        ObjectNode modelNode = objectMapper.createObjectNode();
//        modelNode.put("name", form.getName());
//        modelNode.put("description", form.getDescription());
//        modelNode.put("key", form.getKey());
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, form.getName());
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, form.getDescription());
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, form.getKey());

        model.setName(form.getName());
        model.setKey(form.getKey());
        model.setCategory(form.getCategory());
        model.setMetaInfo(modelNode.toString());
        repositoryService.saveModel(model);

        createObjectNode(model.getId());
        return R.success("创建成功", model.getId());
    }

    /**
     * Describe: 创建流程图节点信息
     * Param: modelId
     * Return: null
     * */
    private void createObjectNode(String modelId){
        //完善ModelEditorSource
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace","http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.put("stencilset", stencilSetNode);
        try {
            repositoryService.addModelEditorSource(modelId,editorNode.toString().getBytes("utf-8"));
        } catch (Exception e) {
            System.out.println("创建模型时完善ModelEditorSource服务异常："+e);
        }
        System.out.println("创建模型完善ModelEditorSource结束");
    }

    /**
     * Describe: 获取流程编辑器视图
     * Param: modelAndView
     * Return: 流程编辑视图
     * */
    @GetMapping("/model/editor")
    public ModelAndView editor(ModelAndView modelAndView){
        modelAndView.setViewName("page/admin/activiti/model/model-editor");
        return modelAndView;
    }

    /**
     * Describe: 根据 Id 删除流程图
     * Param: id
     * Return: Result
     * */
    @PostMapping("/model/delete/{id}")
    public R deleteById(@PathVariable String id){
        repositoryService.deleteModel(id);
        return R.success("删除成功");
    }

    /**
     * Describe: 发布流程
     * Param: id
     * Return: Result
     * */
    @ResponseBody
    @PostMapping("/model/deploy")
    public R publish(String id){
        try {
            Model modelData = repositoryService.getModel(id);
            byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
            if (bytes == null) {
                return R.error("模型数据为空，请先设计流程并成功保存，再进行发布。");
            }
            JsonNode modelNode = new ObjectMapper().readTree(bytes);
            log.debug("modelNode = {}", modelNode.toString());
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if (model.getProcesses().size() == 0) {
                return R.error("数据模型不符要求，请至少设计一条主线流程。");
            }

            Deployment deployment = repositoryService.createDeployment()
                    .name(modelData.getName())
//                    .category(modelData.getCategory())
//                    .key(modelData.getKey())
                    .addBpmnModel(modelData.getKey()+".bpmn20.xml", model)
                    .deploy();

            // 发布版本+1
            Integer version = modelData.getVersion();
            modelData.setVersion(StrUtil.isBlank(modelData.getDeploymentId()) ? version : version + 1);
            modelData.setDeploymentId(deployment.getId());
            repositoryService.saveModel(modelData);

            return R.success("部署成功");
        } catch (Exception e) {
            log.error("模型部署异常", e);
            return R.error("部署异常");
        }
    }

    /**
     * Describe: 流程定义
     * Param: modelAndView
     * Return: 流程定义
     * */
    @GetMapping("/process/init")
    public ModelAndView processInit(ModelAndView modelAndView){
        modelAndView.setViewName("page/admin/activiti/process/process-list");
        return modelAndView;
    }

    /**
     * Describe: 获取流程模定义表数据
     * Param: modelAndView
     * Return: R
     * */
    @GetMapping("process/page")
    public R processPage(PageInfo<SysDict> pageInfo, ActivitiModelVo form){
        R r = new R();
//        DeploymentQuery query = repositoryService.createDeploymentQuery();
//        if(StringUtils.isNotBlank(form.getCategory())) {
//            query.deploymentCategoryLike(form.getCategory());
//        }
//        List<Deployment> list = query.orderByDeploymentId().desc().listPage((pageInfo.getPage() - 1) * pageInfo.getLimit(), pageInfo.getLimit());

        ProcessDefinitionQuery query = repositoryService.createProcessDefinitionQuery();
        if(StringUtils.isNotBlank(form.getCategory())) {
            query.processDefinitionCategoryLike(form.getCategory());
        }
        if(StringUtils.isNotBlank(form.getName())) {
            query.processDefinitionNameLike(form.getName());
        }
        List<ProcessDefinition> list = query.orderByProcessDefinitionVersion().desc().listPage((pageInfo.getPage() - 1) * pageInfo.getLimit(), pageInfo.getLimit());
        List<ProDefined> data = new ArrayList<>();

        list.forEach(processDefinition -> {
            ProDefined defined = new ProDefined();
            defined.setId(processDefinition.getId());
            defined.setName(processDefinition.getName());
            defined.setVersion(processDefinition.getVersion());
            defined.setKey(processDefinition.getKey());
            defined.setBpmn(processDefinition.getResourceName());
            defined.setPng(processDefinition.getDiagramResourceName());
            defined.setDeploymentId(processDefinition.getDeploymentId());
            defined.setSuspended(processDefinition.isSuspended());

            Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(processDefinition.getDeploymentId()).singleResult();
            if(deployment != null) {
                defined.setName(deployment.getName());
                defined.setCategory(deployment.getCategory());
                defined.setDeploymentTime(deployment.getDeploymentTime());
            }
            data.add(defined);
        });

        long count = query.count();
        return r.successPage(count, data);
    }

    /**
     * Describe: 获取流程资源文件
     * Param: deploymentId
     * Param: resourceName
     * Return: InputStream
     * */
    private InputStream getProcessDefineResource(String deploymentId, String resourceName) {
        return repositoryService.getResourceAsStream(deploymentId, resourceName);
    }

    /**
     * Describe: 获取流程模型列表视图
     * Param: processDefineId
     * Param: resourceName
     * Return: 流程模型列表视图
     * */
    @GetMapping("/process/defined/resource")
    public void getProcessDefineResource(HttpServletResponse response,
                                         @RequestParam("deploymentId") String deploymentId, String resourceName){
        InputStream inputStream = getProcessDefineResource(deploymentId, resourceName);
        byte[] bytes = new byte[1024];
        try {
            // 封装响应内容类型(APPLICATION_OCTET_STREAM 响应的内容不限定)
            response.setContentType("application/octet-stream");
            OutputStream outputStream = response.getOutputStream();
            while (inputStream.read(bytes) != -1) {
                outputStream.write(bytes);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看流程图
     * @param response
     * @param processDefineId
     */
    @GetMapping("/process/defined/image")
    public void getProcessDefineImage(HttpServletResponse response,
                                         @RequestParam("definedId") String processDefineId){
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefineId);
            // 获取流程图图像字符流
            InputStream imageStream = new DefaultProcessDiagramGenerator().generateDiagram(bpmnModel, "png",
                    "宋体", "宋体", "宋体", null, 1.0);
            // 输出资源内容到相应对象
            byte[] b = new byte[1024];
            int len;
            while ((len = imageStream.read(b, 0, 1024)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Describe: 挂起
     * Param: deploymentId
     * Return: Result
     * */
    @PostMapping("/process/defined/suspend/{deploymentId}")
    public R suspend(@PathVariable String deploymentId){
        ProcessDefinition processDefinition = ProcessKit.getProcessDefinition(deploymentId);
        if (!processDefinition.isSuspended()) {
            repositoryService.suspendProcessDefinitionById(processDefinition.getId());
        }
        return R.success("挂起成功");
    }

    /**
     * Describe: 激活
     * Param: deploymentId
     * Return: Result
     * */
    @PostMapping("/process/defined/activate/{deploymentId}")
    public R activate(@PathVariable String deploymentId){
        ProcessDefinition processDefinition = ProcessKit.getProcessDefinition(deploymentId);
        if (processDefinition.isSuspended()) {
            repositoryService.activateProcessDefinitionById(processDefinition.getId());
        }
        return R.success("激活成功");
    }

    /**
     * Describe: 根据 Id 删除流程定义
     * Param: deploymentId
     * Return: Result
     * */
    @DeleteMapping("/process/defined/remove/{deploymentId}")
    public R remove(@PathVariable String deploymentId){
        repositoryService.deleteDeployment(deploymentId,true);
        return R.success("删除成功");
    }

    /********************************** 用户初始化 ************************************************/

    /**
     * 初始化
     * @return
     */
    @RequestMapping("initUser")
    public R initializationUser() {
        //添加用户
        User user1 = identityService.newUser("user1");
        user1.setFirstName("张三");
        user1.setLastName("张");
        user1.setPassword("123456");
        user1.setEmail("zhangsan@qq.com");
        identityService.saveUser(user1);

        User user2 = identityService.newUser("user2");
        user2.setFirstName("李四");
        user2.setLastName("李");
        user2.setPassword("123456");
        user2.setEmail("lisi@qq.com");
        identityService.saveUser(user2);

        User user3 = identityService.newUser("user3");
        user3.setFirstName("王五");
        user3.setLastName("王");
        user3.setPassword("123456");
        user3.setEmail("wangwu@qq.com");
        identityService.saveUser(user3);

        User user4 = identityService.newUser("user4");
        user4.setFirstName("吴六");
        user4.setLastName("吴");
        user4.setPassword("123456");
        user4.setEmail("wuliu@qq.com");
        identityService.saveUser(user4);


        Group group1 = identityService.newGroup("group1");
        group1.setName("员工组");
        group1.setType("员工组");
        identityService.saveGroup(group1);

        Group group2 = identityService.newGroup("group2");
        group2.setName("总监组");
        group2.setType("总监阻");
        identityService.saveGroup(group2);

        Group group3 = identityService.newGroup("group3");
        group3.setName("经理组");
        group3.setType("经理组");
        identityService.saveGroup(group3);

        Group group4 = identityService.newGroup("group4");
        group4.setName("人力资源组");
        group4.setType("人力资源组");
        identityService.saveGroup(group4);

        identityService.createMembership("user1", "group1");//user1 在员工组
        identityService.createMembership("user2", "group2");//user2在总监组
        identityService.createMembership("user3", "group3");//user3在经理组
        identityService.createMembership("user4", "group4");//user4在人力资源组

        return R.success("success");
    }

}
