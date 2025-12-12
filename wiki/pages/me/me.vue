<template>
	<view class="page">
		<view class="header">
			<view class="user">
				<image :src="userInfo.userImg || '/static/logo.png'" mode="aspectFill" @error="handleImageError">
				</image>
				<text v-if="userInfo && userInfo.username">{{userInfo.username}}</text>
				<text v-else class="login-text" @click="goToLogin">去登录</text>
			</view>
			<view class="info">
				<view class="item">
					<text>12</text>
					<text>使用天数</text>
				</view>
				<view class="item">
					<text>43</text>
					<text>记账笔数</text>
				</view>
			</view>
		</view>
		<view class="menu">
			<view class="item" v-for="v in menus" :key="v.id" @click="handleMenuClick(v)">
				<image :src="v.icon"></image>
				<view>{{v.name}}</view>
				<image class="arrow" v-if="v.arrow" src="/static/center/right.png"></image>
			</view>
		</view>
	</view>
</template>
<script>
	export default {
		data() {
			return {
				userInfo: {},
				menus: [{
						id: 1,
						name: '我的购物车',
						icon: '/static/icon/gouwuche.png',
						arrow: true,
						url: '/pages/cart/cart',
						requireLogin: true
					},
					{
						id: 2,
						name: '我的订单',
						icon: '/static/icon/gouwuche.png',
						arrow: true,
						url: '/pages/order/list',
						requireLogin: true
					},
					{
						id: 3,
						name: '联系客服',
						icon: '/static/center/comment.png',
						arrow: true
					},
					{
						id: 4,
						name: '分享应用',
						icon: '/static/center/share.png',
						arrow: false
					},
					{
						id: 5,
						name: '退出登录',
						icon: '/static/center/poweroff.png',
						arrow: false,
						requireLogin: true
					},
				]
			}
		},
		onLoad() {
			this.loadUserInfo();
		},
		onShow() {
			// 每次显示页面时重新加载用户信息，确保数据最新
			this.loadUserInfo();
		},
		methods: {
			// 加载用户信息（优先从本地存储读取）
			loadUserInfo() {
				// 先从本地存储读取用户信息
				const localUserInfo = uni.getStorageSync('userInfo');
				if (localUserInfo && localUserInfo.username) {
					this.userInfo = localUserInfo;
					// 如果有userId，可以尝试从服务器获取最新信息
					if (localUserInfo.userId) {
						this.getUserInfoFromServer(localUserInfo.userId);
					}
				} else {
					// 如果没有本地信息，尝试从服务器获取
					const token = uni.getStorageSync('token');
					if (token) {
						// 如果有token但没有用户信息，可能需要重新登录
						this.userInfo = {};
					} else {
						this.userInfo = {};
					}
				}
			},
			// 跳转到登录页面
			goToLogin() {
				uni.navigateTo({
					url: '/pages/login/login'
				});
			},
			// 从服务器获取用户信息
			getUserInfoFromServer(userId) {
				if (!userId) return;
				
				uni.request({
					url: 'http://localhost:8080/WxUser/findUserInfo',
					method: 'GET',
					data: {
						userId: userId
					},
					success: (res) => {
						if (res.data && res.data.code === 200 && res.data.data) {
							// 更新用户信息，保留本地已有的信息
							this.userInfo = {
								...this.userInfo,
								...res.data.data,
								username: res.data.data.username || this.userInfo.username
							};
							// 更新本地存储
							uni.setStorageSync('userInfo', this.userInfo);
						}
					},
					fail: (err) => {
						console.error('获取用户信息失败:', err);
					}
				});
			},
			// 获取用户信息（保留原方法以兼容）
			getUserInfo() {
				this.loadUserInfo();
			},
			// 检查token状态
			checkToken() {
				const token = uni.getStorageSync('token');
				if (!token) {
					this.userInfo = {};
					return;
				}
				uni.request({
					url: 'http://localhost:8080/WxUser/checkTokenStatus',
					method: 'GET',
					success: (res) => {
						if (res.data && res.data.code === 200) {
							// token有效
							return;
						} else {
							// token无效，清除用户信息
							uni.removeStorageSync('token');
							uni.removeStorageSync('userInfo');
							this.userInfo = {};
						}
					},
					fail: (err) => {
						console.error('检查token失败:', err);
					}
				});
			},
			// 处理菜单点击
			handleMenuClick(item) {
				const token = uni.getStorageSync('token');
				const userInfo = uni.getStorageSync('userInfo');
				// 需要登录但未登录的菜单项
				if (item.requireLogin && (!token || !userInfo)) {
					uni.showToast({
						title: '请先登录',
						icon: 'none'
					});
					this.goToLogin();
					return;
				}
				if (item.id === 5) { // 退出登录
					this.logout();
				} else if (item.url) {
					// 跳转到指定页面
					uni.navigateTo({
						url: item.url
					});
				}
				// 其他菜单项的处理...
			},
			// 退出登录
			logout() {
				uni.showModal({
					title: '提示',
					content: '确定要退出登录吗？',
					success: (res) => {
						if (res.confirm) {
							uni.removeStorageSync('token');
							uni.removeStorageSync('userInfo');
							this.userInfo = {};
							uni.showToast({
								title: '已退出登录',
								icon: 'success'
							});
							// 跳转到首页
							setTimeout(() => {
								uni.reLaunch({
									url: '/pages/index/index'
								});
							}, 1500);
						}
					}
				});
			},
			// 处理图片加载错误
			handleImageError(e) {
				console.log('头像加载失败:', e);
				// 可以设置默认头像
				this.userInfo.userImg = '/static/logo.png';
			}
		}
	}
</script>
<style lang="scss">
	@import "./me.scss";

	.login-text {
		color: #007AFF;
		text-decoration: underline;
	}
</style>