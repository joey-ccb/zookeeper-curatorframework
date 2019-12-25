package org.ccb.demo.zookeeper.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by cuicb on 2019/12/23.
 */
@Component
@Slf4j
public class ZookeeperOps {
    @Autowired
    private CuratorClient client;

    public boolean createNodeAndSetData(String path, CreateMode mode, byte[] data) {
        checkData(path, data);
        try {
            client.getClient()
                    .create()
                    .withMode(mode)
                    .forPath(path, data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean createNode(String path, CreateMode mode) {
        try {
            String rePath = client.getClient()
                    .create()
                    .withMode(mode)
                    .forPath(path, new byte[0]);

            if (path.equals(rePath)) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public byte[] getData(String path) {
        byte[] bytes = null;
        try {
            client.getClient()
                    .getData()
                    .forPath(path);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return bytes;
    }

    public boolean setData(String path, byte[] data) {
        checkData(path, data);
        int dataLength = 0;
        try {
            if (checkExists(path)) {
                dataLength = client.getClient()
                        .setData()
                        .forPath(path, data).getDataLength();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (dataLength > 0) {
            log.info("zk setData()->path=%s success.");
            return true;
        }
        return false;
    }

    /**
     * exists=true;
     * @param path
     * @return
     */
    public boolean checkExists(String path) {
        try {
            Stat stat = client.getClient().
                    checkExists()
                    .forPath(path);
            if (stat != null) {
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    private void checkData(String path, byte[] data) {
        Objects.requireNonNull(data, String.format("zk setData()->path=%s,data is null.", path));
    }
}
