# kill
 
    该系统是bilibili上随便找的，一个基于高并发学习的项目，因学校没有高并发场景的项目实践，故借此学习，做一些笔记。许愿明年大厂🙏🏻

### 1.1在不考虑并发的情况下

    sql语句一般这样写，高并发的环境下会非常危险

```sql

  SELECT
      a.*,
      b.name AS itemName,
      (
        CASE WHEN (now() BETWEEN a.start_time AND a.end_time AND a.total > 0)
          THEN 1
        ELSE 0
        END
      )      AS canKill
    FROM item_kill AS a LEFT JOIN item AS b ON b.id = a.item_id
    WHERE a.is_active = 1
```
```sql
UPDATE item_kill
    SET total = total - 1
    WHERE
        id = #{killId}
```
```java
public Boolean killItem(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        //TODO:判断当前用户是否已经抢购过当前商品
        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
            //TODO:查询待秒杀商品详情
            ItemKill itemKill=itemKillMapper.selectById(killId);
            //TODO:判断是否可以被秒杀canKill=1?
            if (itemKill!=null && 1==itemKill.getCanKill() ){
                //TODO:扣减库存-减一
                int res=itemKillMapper.updateKillItem(killId);
                //TODO:扣减是否成功?是-生成秒杀成功的订单，同时通知用户秒杀成功的消息
                if (res>0){
                    commonRecordKillSuccessInfo(itemKill,userId);
                    result=true;
                }
            }
        }else{
            throw new Exception("您已经抢购过该商品了!");
        }
        return result;
    }
```
### 1.2对sql优化，确保不会把库存击穿，
    虽然确保了不会把库存击穿，但是可能会出现，5个人去抢10个库存，库存为0，但是交易记录只有5条
```sql
SELECT
      a.*,
      b.name AS itemName,
      (CASE WHEN (now() BETWEEN a.start_time AND a.end_time)
        THEN 1
       ELSE 0
       END)  AS canKill
    FROM item_kill AS a LEFT JOIN item AS b ON b.id = a.item_id
    WHERE a.is_active = 1 AND a.id =#{id} AND a.total>0
```
```sql
 UPDATE item_kill
    SET total = total - 1
    WHERE id = #{killId} AND total>0
```
### 1.3对用redis的NX操作，设置锁
    但是setIfAbsent和expire是分开的，在中间宕机会出现死锁问题
```java
public Boolean killItemV3(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){

            //TODO:借助Redis的原子操作实现分布式锁-对共享操作-资源进行控制
            ValueOperations valueOperations=stringRedisTemplate.opsForValue();
            //TODO: 以 killId+userId+"-RedisLock"作为key，value可以随意，生成快就行
            final String key=new StringBuffer().append(killId).append(userId).append("-RedisLock").toString();
            final String value=RandomUtil.generateOrderCode();
            Boolean cacheRes=valueOperations.setIfAbsent(key,value); //luna脚本提供“分布式锁服务”，就可以写在一起
            //TOOD:redis部署节点宕机了，如果在这里宕机会出现死锁问题
            if (cacheRes){
                //TODO: 设置成功后再为key设置过期时间
                stringRedisTemplate.expire(key,30, TimeUnit.SECONDS);

                try {
                    ItemKill itemKill=itemKillMapper.selectByIdV2(killId);
                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal()>0){
                        int res=itemKillMapper.updateKillItemV2(killId);
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);

                            result=true;
                        }
                    }
                }catch (Exception e){
                    throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
                }finally {
                    //TODO：释放锁
                    if (value.equals(valueOperations.get(key).toString())){
                        stringRedisTemplate.delete(key);
                    }
                }
            }
        }else{
            throw new Exception("Redis-您已经抢购过该商品了!");
        }
        return result;
    }
```
### 1.4使用redisson分布式锁，相比于zookeeper，还是redisson更快
```java
public Boolean killItemV4(Integer killId, Integer userId) throws Exception {
        Boolean result=false;

        final String lockKey=new StringBuffer().append(killId).append(userId).append("-RedissonLock").toString();
        RLock lock=redissonClient.getLock(lockKey);

        try {
            Boolean cacheRes=lock.tryLock(30,10,TimeUnit.SECONDS);
            if (cacheRes){
                //TODO:核心业务逻辑的处理
                if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
                    ItemKill itemKill=itemKillMapper.selectByIdV2(killId);
                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal()>0){
                        int res=itemKillMapper.updateKillItemV2(killId);
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);

                            result=true;
                        }
                    }
                }else{
                    throw new Exception("redisson-您已经抢购过该商品了!");
                }
            }
        }finally {
            lock.unlock();
            //lock.forceUnlock();
        }
        return result;
    }
```
### 1.5使用zookeeper分布式锁
```java
public Boolean killItemV5(Integer killId, Integer userId) throws Exception {
        Boolean result=false;
        // TODO: 在注册中心注册唯一的锁，类似于文件夹树形结构
        InterProcessMutex mutex=new InterProcessMutex(curatorFramework,pathPrefix+killId+userId+"-lock");
        try {
            if (mutex.acquire(10L,TimeUnit.SECONDS)){

                //TODO:核心业务逻辑
                if (itemKillSuccessMapper.countByKillUserId(killId,userId) <= 0){
                    ItemKill itemKill=itemKillMapper.selectByIdV2(killId);
                    if (itemKill!=null && 1==itemKill.getCanKill() && itemKill.getTotal()>0){
                        int res=itemKillMapper.updateKillItemV2(killId);
                        if (res>0){
                            commonRecordKillSuccessInfo(itemKill,userId);
                            result=true;
                        }
                    }
                }else{
                    throw new Exception("zookeeper-您已经抢购过该商品了!");
                }
            }
        }catch (Exception e){
            throw new Exception("还没到抢购日期、已过了抢购时间或已被抢购完毕！");
        }finally {
            if (mutex!=null){
                mutex.release();
            }
        }
        
        return result;
    }
```
## 生成分布式id
### 2.1 传统方法，时间戳+N位随机流水号
```java
public static String generateOrderCode(){
        //TODO: 时间戳 + N位随机数流水号
        return dateFormatOne.format(DateTime.now().toDate()) + generateNumber(4);
    }

    public static String generateNumber(final int num){
        // 在高并发下用buffer保证安全
        StringBuffer sb = new StringBuffer();
        for(int i = 1; i <= num; i++){
            sb.append(random.nextInt(9));
        }
        return sb.toString();

    }
```
### 2.2 雪花算法，高效生成id，且递增
    直接调用雪花算法的api
```java
public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(2, 3);

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            System.out.println("当前生成的有序数字串："+snowFlake.nextId());
        }

        System.out.println("总共耗时："+(System.currentTimeMillis() - start));
    }
```
