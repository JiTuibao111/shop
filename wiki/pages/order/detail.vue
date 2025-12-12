<template>
	<view class="container">
		<!-- 订单状态 -->
		<view class="status-bar">
			<text class="status-text">{{ getStatusText(orderDetail.status) }}</text>
		</view>
		
		<!-- 收货信息 -->
		<view class="section">
			<view class="section-title">收货信息</view>
			<view class="info-item">
				<text class="label">收货人：</text>
				<text class="value">{{ orderDetail.receiverName }}</text>
			</view>
			<view class="info-item">
				<text class="label">联系电话：</text>
				<text class="value">{{ orderDetail.receiverPhone }}</text>
			</view>
			<view class="info-item">
				<text class="label">收货地址：</text>
				<text class="value">{{ orderDetail.receiverAddress }}</text>
			</view>
			<view class="info-item" v-if="orderDetail.remark">
				<text class="label">备注：</text>
				<text class="value">{{ orderDetail.remark }}</text>
			</view>
		</view>
		
		<!-- 商品信息 -->
		<view class="section">
			<view class="section-title">商品信息</view>
			<view class="goods-item" v-for="item in orderDetail.orderItems" :key="item.itemId">
				<image :src="item.goods?.image || '/static/logo.png'" mode="aspectFill" class="goods-image"></image>
				<view class="goods-info">
					<view class="goods-name">{{ item.goods?.goodsName || '商品名称' }}</view>
					<view class="goods-price-quantity">
						<text class="price">¥{{ formatPrice(item.price) }}</text>
						<text class="quantity">x{{ item.quantity }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 订单信息 -->
		<view class="section">
			<view class="section-title">订单信息</view>
			<view class="info-item">
				<text class="label">订单号：</text>
				<text class="value">{{ orderDetail.orderNo }}</text>
			</view>
			<view class="info-item">
				<text class="label">创建时间：</text>
				<text class="value">{{ formatTime(orderDetail.createTime) }}</text>
			</view>
			<view class="info-item">
				<text class="label">订单金额：</text>
				<text class="value price">¥{{ formatPrice(orderDetail.totalAmount) }}</text>
			</view>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="bottom-bar" v-if="orderDetail.status === 0 || orderDetail.status === 1">
			<view 
				class="action-btn cancel-btn" 
				v-if="orderDetail.status === 0 || orderDetail.status === 1"
				@click="cancelOrder"
			>
				取消订单
			</view>
			<view 
				class="action-btn pay-btn" 
				v-if="orderDetail.status === 0"
				@click="payOrder"
			>
				去支付
			</view>
		</view>
	</view>
</template>

<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				orderId: null,
				orderDetail: {}
			}
		},
		onLoad(options) {
			this.$api = api;
			if (options.id) {
				this.orderId = parseInt(options.id);
				this.getOrderDetail();
			}
		},
		methods: {
			// 获取订单详情
			async getOrderDetail() {
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
							}
						}
					});
					return;
				}
				
				try {
					const res = await this.$api.get(`/order/detail/${this.orderId}`);
					if (res && res.data) {
						this.orderDetail = res.data;
					}
				} catch (error) {
					console.error('获取订单详情失败:', error);
				}
			},
			
			// 获取状态文本
			getStatusText(status) {
				const statusMap = {
					0: '待支付',
					1: '已支付',
					2: '已发货',
					3: '已完成',
					4: '已取消'
				};
				return statusMap[status] || '未知';
			},
			
			// 取消订单
			async cancelOrder() {
				uni.showModal({
					title: '提示',
					content: '确定要取消这个订单吗？',
					success: async (res) => {
						if (res.confirm) {
							try {
								const result = await this.$api.put(`/order/cancel/${this.orderId}`);
								if (result && result.code === 200) {
									uni.showToast({
										icon: 'success',
										title: '订单已取消'
									});
									this.getOrderDetail();
								}
							} catch (error) {
								console.error('取消订单失败:', error);
							}
						}
					}
				});
			},
			
			// 支付订单
			payOrder() {
				uni.showToast({
					icon: 'none',
					title: '支付功能待开发'
				});
			},
			
			// 格式化价格
			formatPrice(price) {
				if (!price) return '0.00';
				return price.toFixed(2);
			},
			
			// 格式化时间
			formatTime(time) {
				if (!time) return '';
				const date = new Date(time);
				const year = date.getFullYear();
				const month = String(date.getMonth() + 1).padStart(2, '0');
				const day = String(date.getDate()).padStart(2, '0');
				const hour = String(date.getHours()).padStart(2, '0');
				const minute = String(date.getMinutes()).padStart(2, '0');
				return `${year}-${month}-${day} ${hour}:${minute}`;
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container {
		background-color: #f5f5f5;
		min-height: 100vh;
		padding-bottom: 120rpx;
	}
	
	.status-bar {
		background: linear-gradient(to right, #1296db, #4fc3f7);
		padding: 60rpx 30rpx;
		text-align: center;
		
		.status-text {
			font-size: 36rpx;
			color: #fff;
			font-weight: bold;
		}
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
		
		.info-item {
			display: flex;
			padding: 15rpx 0;
			font-size: 28rpx;
			
			.label {
				color: #999;
				width: 150rpx;
			}
			
			.value {
				color: #333;
				flex: 1;
				
				&.price {
					color: #ff4444;
					font-weight: bold;
				}
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
					
					.price {
						font-size: 30rpx;
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
		justify-content: flex-end;
		height: 100rpx;
		background-color: #fff;
		border-top: 1rpx solid #eee;
		padding: 0 20rpx;
		box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.1);
		gap: 20rpx;
		
		.action-btn {
			padding: 20rpx 40rpx;
			border-radius: 50rpx;
			font-size: 28rpx;
		}
		
		.cancel-btn {
			background-color: #f5f5f5;
			color: #666;
		}
		
		.pay-btn {
			background-color: #ff4444;
			color: #fff;
		}
	}
</style>

