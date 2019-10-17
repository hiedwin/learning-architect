package com.edwin.practive.springbootmvcframe.modules.sys.web;

import com.edwin.practive.springbootmvcframe.common.json.AjaxJson;
import com.edwin.practive.springbootmvcframe.common.utils.StringUtils;
import com.edwin.practive.springbootmvcframe.core.web.BaseController;
import com.edwin.practive.springbootmvcframe.modules.sys.entity.Menu;
import com.edwin.practive.springbootmvcframe.modules.sys.entity.Role;
import com.edwin.practive.springbootmvcframe.modules.sys.service.IMenuService;
import com.edwin.practive.springbootmvcframe.modules.sys.service.IRoleService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Menu Controller
 * @author Edwin
 */
@Controller
@RequestMapping("menu")
public class MenuController extends BaseController {


    @Reference(version = "1.0.0")
    IMenuService menuService;
    @Reference(version = "1.0.0")
    IRoleService roleService;

    /**
     * 进入 默认查询一条数据
     * @param id
     * @return
     */
    @ModelAttribute
    public Menu get(@RequestParam(required=false) String id) {
        Menu entity = null;
        if (StringUtils.isNotBlank(id)){
            entity = menuService.get(id);
        }
        if (entity == null){
            entity = new Menu();
        }
        return entity;
    }

    @RequestMapping("form")
    public String form(Menu menu,String flag,ModelMap map){
        menu.setParent(menuService.getMenuMap(menu.getParentId()));

        if("add".equals(flag)){
            Menu parent = menu;
            menu = new Menu();
            menu.setParent(parent);
            menu.setParentId(parent.getId());
        }

        map.addAttribute("menu",menu);
        return "modules/sys/menu/menu_save";
    }

    @RequestMapping("treeDataForm")
    public String treeDataForm(String selectIds,Menu menu,ModelMap map){
        map.addAttribute("selectIds",selectIds);
        map.addAttribute("menu",menu);
        return "modules/sys/menu/menu_tree_select";
    }

    @RequestMapping("treeDataByRoleForm")
    public String treeDataByRoleForm(String selectIdFlag,Menu menu,ModelMap map){
        map.addAttribute("selectIdFlag",selectIdFlag);
        return "modules/sys/menu/menu_role_tree_select";
    }



    @RequestMapping("treeByRoleData")
    @ResponseBody
    public List<Map<String, Object>> treeByRoleData(String selectIdFlag,Menu menu,ModelMap modelMap){

        List<Menu> menusTemp = menuService.findListAll();
        List<Menu> menus = new ArrayList<>();

        Menu tree = menuService.getMenuTrees(menusTemp);
        // 递归处理数据
        menuService.recursionMenu(menus,tree);


        String[] menuIds = roleService.getRoleMenu(new Role(selectIdFlag));

        // 选中数量
        int count = 0;

        List<Map<String, Object>> mapList = Lists.newArrayList();
        for (Menu me : menus) {
            // 获取父节点元素
            //me.setParent(menuService.getMenuMap(me.getParentId()));

            Map<String, Object> map = Maps.newHashMap();
            map.put("id", me.getId());
            if("0".equals(me.getParentId())){
                map.put("parent", "#");
                Map<String, Object> state = Maps.newHashMap();
                state.put("opened", true);
                map.put("state", state);

            }else{
                /*if(i == 0){
                    map.put("parent", "#");
                }else{
                    map.put("parent", e.getParentId());
                }*/
                map.put("parent", me.getParentId());
            }

            // 处理 角色菜单
            // 选中
            List<String> selectIdsList = new ArrayList<>(Arrays.asList(menuIds));
            if(null != selectIdsList && selectIdsList.contains(me.getId())){
                Map<String, Object> state = Maps.newHashMap();
                state.put("opened", true);
                state.put("selected", true);
                map.put("state", state);
                count++;
            }



            if(StringUtils.isNotBlank(me.getIcon())){
                map.put("icon", me.getIcon());
            }
            if("2".equals(me.getType())){
                map.put("type", "btn");
            }else if("1".equals(me.getType())){
                map.put("type", "menu");
            }else{
                map.put("type", "menu");
            }
            map.put("text", me.getName());
            map.put("name", me.getName());

            mapList.add(map);
        }

        System.out.println(count);

        return mapList;

    }


    @RequestMapping("treeData")
    @ResponseBody
    public List<Map<String, Object>> treeData(String selectIdFlag,Menu menu,ModelMap modelMap){

        List<Menu> menus = menuService.findListAll();

        List<Map<String, Object>> mapList = Lists.newArrayList();
        for (Menu me : menus) {
            // 获取父节点元素
            me.setParent(menuService.getMenuMap(me.getParentId()));

            Map<String, Object> map = Maps.newHashMap();
            map.put("id", me.getId());
            if("0".equals(me.getParentId())){
                map.put("parent", "#");
                Map<String, Object> state = Maps.newHashMap();
                state.put("opened", true);
                map.put("state", state);

            }else{
                /*if(i == 0){
                    map.put("parent", "#");
                }else{
                    map.put("parent", e.getParentId());
                }*/
                map.put("parent", me.getParentId());
            }

            // 处理 角色菜单

            /*int count = 0;
            for (int i1 = 0; i1 < list.size(); i1++) {
                if (list.get(i1).getParentId().equals(e.getId())){
                    count ++;
                }
            }
            if(menuIds.contains(","+e.getId()+",")&& count == 0){
                Map<String, Object> state = Maps.newHashMap();
                state.put("selected", true);
                map.put("state", state);
            }*/

            if(StringUtils.isNotBlank(me.getIcon())){
                map.put("icon", me.getIcon());
            }
            if("2".equals(me.getType())){
                map.put("type", "btn");
            }else if("1".equals(me.getType())){
                map.put("type", "menu");
            }else{
                map.put("type", "menu");
            }
            map.put("text", me.getName());
            map.put("name", me.getName());

            mapList.add(map);
        }
        return mapList;

    }

    @RequestMapping("list")
    public String list(ModelMap map){
         return "modules/sys/menu/menu_list";
    }

    @RequestMapping("data")
    @ResponseBody
    public List<Menu> data(Menu menu, ModelMap map){
        return menuService.findListAll();
    }



    @PostMapping("save")
    @ResponseBody
    public AjaxJson save(Menu menu, ModelMap map){
        AjaxJson j = new AjaxJson();

        /**
         * 后台hibernate-validation 插件校验
         */
        String errMsg = beanValidator(menu);
        if (StringUtils.isNotBlank(errMsg)){
            j.setSuccess(false);
            j.setMsg(errMsg);
            return j;
        }

        menuService.save(menu);
        j.setMsg("保存成功！");
        return j;
    }

    @RequestMapping("del")
    @ResponseBody
    public AjaxJson del(Menu menu, ModelMap map){
        AjaxJson j = new AjaxJson();
        menuService.delete(menu);
        j.setMsg("删除成功！");
        return j;
    }

    @RequestMapping("delAll")
    @ResponseBody
    public AjaxJson delAll(@RequestBody List<Map<String,String>>  list, ModelMap map){
        AjaxJson j = new AjaxJson();
        String[] idArray = new String[list.size()];
        for(int i = 0; i<list.size(); i++){
            idArray[i] = list.get(i).get("id");
        }

        for(String id : idArray){
            Menu m = new Menu();
            m.setId(id);
            menuService.delete(m);
        }
        j.setMsg("删除成功");
        return j;
    }

}