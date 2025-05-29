package dev.lvstrng.base.utils;

import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;

import java.util.HashMap;
import java.util.Map;

public class SwitchUtils {
    public static Map<Integer, LabelNode> tableToMap(TableSwitchInsnNode table) {
        var map = new HashMap<Integer, LabelNode>();

        for (int i = 0; i < table.labels.size(); i++) {
            int key = table.min + i;
            var label = table.labels.get(i);
            map.put(key, label);
        }

        return map;
    }

    public static Map<Integer, LabelNode> lookupToMap(LookupSwitchInsnNode lookup) {
        var map = new HashMap<Integer, LabelNode>();

        for(int i = 0; i < lookup.keys.size(); i++) {
            int key = lookup.keys.get(i);
            var label = lookup.labels.get(i);
            map.put(key, label);
        }

        return map;
    }
}
