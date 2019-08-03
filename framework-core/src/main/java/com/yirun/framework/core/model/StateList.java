package com.yirun.framework.core.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 带有状态的List，用于限制查询
 */
public  class StateList<T extends Number> extends ArrayList {

    /**
     * 是否继续进行
     */
    private boolean isProceed;

    public StateList() {
        super(0);
    }

    public StateList(Collection collection) {
        super(collection);
    }

    public StateList haltProceed() {
        this.isProceed = this.size() > 0;
        if (!this.isProceed) {
            this.add(Integer.valueOf(-999));
        }
        return this;
    }

    public StateList proceed() {
        this.isProceed = true;
        return this;
    }

    public boolean isProceed() {
        return isProceed;
    }

}