package com.dossy.planegame.factory;

import android.content.res.Resources;

import com.dossy.planegame.bullet.BossRHellfireBullet;
import com.dossy.planegame.bullet.BossSunBullet;
import com.dossy.planegame.bullet.BossYHellfireBullet;
import com.dossy.planegame.plane.BigPlane;
import com.dossy.planegame.bullet.BigPlaneBullet;
import com.dossy.planegame.bullet.BossFlameBullet;
import com.dossy.planegame.bullet.BossDefaultBullet;
import com.dossy.planegame.plane.BossPlane;
import com.dossy.planegame.object.PurpleBulletGoods;
import com.dossy.planegame.object.RedBulletGoods;
import com.dossy.planegame.object.GameObject;
import com.dossy.planegame.object.LifeGoods;
import com.dossy.planegame.plane.MiddlePlane;
import com.dossy.planegame.object.MissileGoods;
import com.dossy.planegame.bullet.MyBlueBullet;
import com.dossy.planegame.bullet.MyPurpleBullet;
import com.dossy.planegame.bullet.MyRedBullet;
import com.dossy.planegame.plane.MyPlane;
import com.dossy.planegame.plane.SmallPlane;


/**
 * 物品构建的工厂
 */
public class GameObjectFactory {
    /**
     * 小型机
     * @param resources
     * @return
     */
    public GameObject createSmallPlane(Resources resources) {
        return new SmallPlane(resources);
    }

    /**
     * 生产中型机
     * @param resources
     * @return
     */
    public GameObject createMiddlePlane(Resources resources) {
        return new MiddlePlane(resources);
    }

    /**
     * 生产大型机
     * @param resources
     * @return
     */
    public GameObject createBigPlane(Resources resources) {
        return new BigPlane(resources);
    }

    /**
     * 生产boss机体
     * @param resources
     * @return
     */
    public GameObject createBossPlane(Resources resources) {
        return new BossPlane(resources);
    }

    /**
     * 生产我方机体
     * @param resources
     * @return
     */
    public GameObject createMyPlane(Resources resources) {
        return new MyPlane(resources);
    }

    /**
     * 生产我方的子弹
     * @param resources
     * @return
     */
    public GameObject createMyBlueBullet(Resources resources) {
        return new MyBlueBullet(resources);
    }
    public GameObject createMyPurpleBullet(Resources resources) {
        return new MyPurpleBullet(resources);
    }
    public GameObject createMyRedBullet(Resources resources) {
        return new MyRedBullet(resources);
    }

    /**
     * 生产BOSS的子弹
     * @param resources
     * @return
     */
    public GameObject createBossFlameBullet(Resources resources) {
        return new BossFlameBullet(resources);
    }

    public GameObject createBossYHellfireBullet(Resources resources) {
        return new BossYHellfireBullet(resources);
    }

    public GameObject createBossRHellfireBullet(Resources resources) {
        return new BossRHellfireBullet(resources);
    }

    public GameObject createBossBulletDefault(Resources resources) {
        return new BossDefaultBullet(resources);
    }

    public GameObject createBossSunBullet(Resources resources) {
        return new BossSunBullet(resources);
    }

    /**
     * 生产BigPlane的子弹
     * @param resources
     * @return
     */
    public GameObject createBigPlaneBullet(Resources resources) {
        return new BigPlaneBullet(resources);
    }

    /**
     * 生产导弹物品
     * @param resources
     * @return
     */
    public GameObject createMissileGoods(Resources resources) {
        return new MissileGoods(resources);
    }

    /**
     * 生产生命物品
     * @param resources
     * @return
     */
    public GameObject createLifeGoods(Resources resources) {
        return new LifeGoods(resources);
    }


    /**
     * 生产子弹物品
     * @param resources
     * @return
     */
    public GameObject createPurpleBulletGoods(Resources resources) {
        return new PurpleBulletGoods(resources);
    }

    public GameObject createRedBulletGoods(Resources resources) {
        return new RedBulletGoods(resources);
    }
}
