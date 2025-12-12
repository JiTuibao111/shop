<template>
	<view class="container">
		<!-- 收货信息 -->
		<view class="section">
			<view class="section-title">收货信息</view>
			<view class="form-item">
				<text class="label">收货人姓名</text>
				<input class="input" v-model="receiverName" placeholder="请输入收货人姓名" />
			</view>
			<view class="form-item">
				<text class="label">联系电话</text>
				<input class="input" v-model="receiverPhone" type="number" placeholder="请输入联系电话" />
			</view>
			<view class="form-item">
				<text class="label">收货地址</text>
				<textarea class="textarea" v-model="receiverAddress" placeholder="请输入详细收货地址" />
			</view>
			<view class="form-item">
				<text class="label">备注</text>
				<textarea class="textarea" v-model="remark" placeholder="选填" />
			</view>
		</view>
		
		<!-- 商品列表 -->
		<view class="section">
			<view class="section-title">商品信息</view>
			<view class="goods-item" v-for="item in orderGoods" :key="item.goodsId">
				<image :src="item.image || '/static/logo.png'" mode="aspectFill" class="goods-image"></image>
				<view class="goods-info">
					<view class="goods-name">{{ item.goodsName }}</view>
					<view class="goods-price-quantity">
						<text class="price">¥{{ formatPrice(item.price) }}</text>
						<text class="quantity">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部结算栏 -->
		<view class="bottom-bar">
			<view class="total-price">
				合计：<text class="price-symbol">¥</text>{{ formatPrice(totalAmount) }}
			</view>
			<view class="submit-btn" @click="submitOrder">
				<text>提交订单</text>
			</view>
		</view>
	</view>
</template>

<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				cartIds: [],
				orderGoods: [],
				receiverName: '',
				receiverPhone: '',
				receiverAddress: '',
				remark: '',
				isDirectBuy: false // 是否立即购买
			}
		},
		onLoad(options) {
			this.$api = api;
			
			// 检查登录状态
			const token = uni.getStorageSync('token');
			if (!token) {
				uni.showModal({
					title: '提示',
					content: '请先登录',
					success: (res) => {
						if (res.confirm) {
							uni.navigateTo({
								url: '/pages/login/login'
							});
						} else {
							uni.navigateBack();
						}
					}
				});
				return;
			}
			
			// 获取参数
			if (options.cartIds) {
				this.cartIds = options.cartIds.split(',').map(id => parseInt(id));
				this.isDirectBuy = false;
				this.getCartList();
			} else if (options.goodsId && options.quantity) {
				// 立即购买
				this.isDirectBuy = true;
				this.getGoodsDetail(parseInt(options.goodsId), parseInt(options.quantity));
			}
		},
		computed: {
			totalAmount() {
				return this.orderGoods.reduce((sum, item) => {
					return sum + (item.price * item.quantity);
				}, 0);
			}
		},
		methods: {
			// 获取购物车列表
			async getCartList() {
				try {
					const res = await this.$api.get('/cart/list');
					if (res && res.data) {
						const cartList = res.data;
						this.orderGoods = cartList
							.filter(cart => this.cartIds.includes(cart.cartId))
							.map(cart => ({
								goodsId: cart.goodsId,
								goodsName: cart.goods?.goodsName || '商品名称',
								image: cart.goods?.image || '/static/logo.png',
								price: cart.goods?.price || 0,
								quantity: cart.quantity
							}));
					}
				} catch (error) {
					console.error('获取购物车失败:', error);
				}
			},
			
			// 获取商品详情（立即购买）
			async getGoodsDetail(goodsId, quantity) {
				try {
					const res = await this.$api.get(`/goods/getById/${goodsId}`, {}, {
						loading: true,
						showError: true
					});
					if (res && res.code === 200 && res.data) {
						const goods = res.data;
						this.orderGoods = [{
							goodsId: goods.goodsId,
							goodsName: goods.goodsName,
							image: goods.image || '/static/logo.png',
							price: goods.price,
							quantity: quantity
						}];
					} else {
						uni.showToast({
							icon: 'none',
							title: res?.message || '获取商品信息失败'
						});
					}
				} catch (error) {
					console.error('获取商品详情失败:', error);
					uni.showToast({
						icon: 'none',
						title: '获取商品信息失败'
					});
				}
			},
			
			// 提交订单
			async submitOrder() {
				// 表单验证
				if (!this.receiverName.trim()) {
					uni.showToast({
						icon: 'none',
						title: '请输入收货人姓名'
					});
					return;
				}
				if (!this.receiverPhone.trim()) {
					uni.showToast({
						icon: 'none',
						title: '请输入联系电话'
					});
					return;
				}
				if (!this.receiverAddress.trim()) {
					uni.showToast({
						icon: 'none',
						title: '请输入收货地址'
					});
					return;
				}
				
				// 手机号验证
				const phoneReg = /^1[3-9]\d{9}$/;
				if (!phoneReg.test(this.receiverPhone)) {
					uni.showToast({
						icon: 'none',
						title: '请输入正确的手机号'
					});
					return;
				}
				
				// 立即购买需要先添加到购物车
				if (this.isDirectBuy && this.orderGoods.length > 0) {
					const goods = this.orderGoods[0];
					try {
						// 先添加到购物车
						const cartRes = await this.$api.post('/cart/add', {
							goodsId: goods.goodsId,
							quantity: goods.quantity
						}, {
							loading: false,
							showError: true
						});
						
						if (cartRes && cartRes.code === 200) {
							// 获取购物车列表，找到该商品（可能是新添加的，也可能是合并的）
							const cartListRes = await this.$api.get('/cart/list', {}, {
								loading: false,
								showError: false
							});
							if (cartListRes && cartListRes.data) {
								// 找到该商品在购物车中的记录（可能数量已合并）
								const cartItem = cartListRes.data.find(item => 
									item.goodsId === goods.goodsId
								);
								if (cartItem) {
									this.cartIds = [cartItem.cartId];
									// 更新显示的数量（如果购物车中数量不同）
									if (cartItem.quantity !== goods.quantity) {
										this.orderGoods[0].quantity = cartItem.quantity;
									}
								} else {
									throw new Error('无法找到购物车中的商品');
								}
							} else {
								throw new Error('获取购物车列表失败');
							}
						} else {
							throw new Error(cartRes?.message || '添加到购物车失败');
						}
					} catch (error) {
						console.error('添加到购物车失败:', error);
						uni.showToast({
							icon: 'none',
							title: error.message || '添加到购物车失败，请重试'
						});
						return;
					}
				}
				
				if (this.cartIds.length === 0) {
					uni.showToast({
						icon: 'none',
						title: '请选择商品'
					});
					return;
				}
				
				uni.showLoading({
					title: '提交中...'
				});
				
				try {
					const res = await this.$api.post('/order/create', {
						cartIds: this.cartIds,
						receiverName: this.receiverName.trim(),
						receiverPhone: this.receiverPhone.trim(),
						receiverAddress: this.receiverAddress.trim(),
						remark: this.remark.trim()
					});
					
					uni.hideLoading();
					
					if (res && res.code === 200) {
						uni.showToast({
							icon: 'success',
							title: '订单创建成功'
						});
						
						// 跳转到订单详情
						setTimeout(() => {
							uni.redirectTo({
								url: `/pages/order/detail?id=${res.data.orderId}`
							});
						}, 1500);
					}
				} catch (error) {
					uni.hideLoading();
					console.error('创建订单失败:', error);
				}
			},
			
			// 格式化价格
			formatPrice(price) {
				if (!price) return '0.00';
				return price.toFixed(2);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		padding-bottom: 120rpx;
		background-color: #f5f5f5;
		min-height: 100vh;
	}
	
	.section {
		background-color: #fff;
		margin: 20rpx;
		border-radius: 20rpx;
		padding: 30rpx;
		
		.section-title {
			font-size: 32rpx;
			font-weight: bold;
			color: #333;
			margin-bottom: 30rpx;
			padding-bottom: 20rpx;
			border-bottom: 1rpx solid #eee;
		}
		
		.form-item {
			margin-bottom: 30rpx;
			
			.label {
				display: block;
				font-size: 28rpx;
				color: #333;
				margin-bottom: 10rpx;
			}
			
			.input {
				width: 100%;
				padding: 20rpx;
				border: 1rpx solid #eee;
				border-radius: 10rpx;
				font-size: 28rpx;
			}
			
			.textarea {
				width: 100%;
				min-height: 150rpx;
				padding: 20rpx;
				border: 1rpx solid #eee;
				border-radius: 10rpx;
				font-size: 28rpx;
			}
		}
		
		.goods-item {
			display: flex;
			padding: 20rpx 0;
			border-bottom: 1rpx solid #eee;
			
			&:last-child {
				border-bottom: none;
			}
			
			.goods-image {
				width: 150rpx;
				height: 150rpx;
				border-radius: 10rpx;
				margin-right: 20rpx;
			}
			
			.goods-info {
				flex: 1;
				display: flex;
				flex-direction: column;
				justify-content: space-between;
				
				.goods-name {
					font-size: 28rpx;
					color: #333;
					margin-bottom: 10rpx;
				}
				
				.goods-price-quantity {
					display: flex;
					justify-content: space-between;
					align-items: center;
					
					.price {
						font-size: 32rpx;
						color: #ff4444;
						font-weight: bold;
					}
					
					.quantity {
						font-size: 28rpx;
						color: #999;
					}
				}
			}
		}
	}
	
	.bottom-bar {
		position: fixed;
		bottom: 0;
		left: 0;
		right: 0;
		display: flex;
		align-items: center;
		height: 100rpx;
		background-color: #fff;
		border-top: 1rpx solid #eee;
		padding: 0 20rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
		
		.total-price {
			flex: 1;
			font-size: 32rpx;
			color: #ff4444;
			font-weight: bold;
			
			.price-symbol {
				font-size: 24rpx;
			}
		}
		
		.submit-btn {
			padding: 20rpx 60rpx;
			background-color: #ff4444;
			color: #fff;
			border-radius: 50rpx;
			font-size: 30rpx;
			font-weight: bold;
		}
	}
</style>

