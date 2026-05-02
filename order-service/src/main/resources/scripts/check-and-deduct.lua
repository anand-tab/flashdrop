--KEYS[1] = flash : product_id
--ARGV[1] = requested quantity

local key = KEYS[1]
local requested = tonumber(ARGV[1])

-- check if product exists in redis
local current = tonumber(redis.call('GET', key))


-- Product not in redis = not a flash sale item
if current == nil then
    return -1;
end

-- not enough stock
if current < requested then
    return -2
end


-- All good - deduct automatically and return remaining stock
redis.call('DECRBY' , key , requested)
return current -requested

