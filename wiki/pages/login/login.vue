<template>
	<view class="content">
		<view class="loginBox">
			<h3 style="text-align: center;margin-bottom:60rpx;">欢迎登录</h3>
			<view class="inputBox">
				<view class="ipt">
					<uni-icons type="contact" size="24" color="rgb(247,120,172)"></uni-icons>
					<input type="text" value="" v-model="username" placeholder="请输入账号" />
				</view>
				<view class="ipt">
					<uni-icons type="eye" size="24" color="rgb(247,120,172)"></uni-icons>
					<input :type="showPassword ? 'text' : 'password'" value="" v-model="password" placeholder="请输入密码" />
					<view @click="togglePassword" style="margin-left:20rpx; padding:0 20rpx;">
						<uni-icons :type="showPassword ? 'eye-slash' : 'eye'" size="24" color="#999"></uni-icons>
					</view>
				</view>

				<view class="forgetPwd">
					<span @click="toForgotPassword">忘记密码</span>
					<span @click="toRegister">没有账号，去注册</span>
				</view>
				<button @click="login" :disabled="loading">
					{{ loading ? '登录中...' : '登录' }}
				</button>
			</view>
			<view class="tipbox">
				<view class="txt">
					—— 其他账号登录 ——
				</view>
				<view class="otherUser">
					<uni-icons type="qq" size="40" color="rgb(66,157,250)" @click="qqLogin"></uni-icons>
					<uni-icons type="weixin" size="40" color="rgb(2,187,17)" @click="wechatLogin"></uni-icons>
				</view>
			</view>
		</view>
		<view class="tip">
			某某应用 2024
		</view>
	</view>
</template>

<script>
	import api from '@/utils/api.js';
	
	export default {
		data() {
			return {
				username: "",
				password: "",
				showPassword: false,
				loading: false
			}
		},
		onLoad() {
			// 将api挂载到this上
			this.$api = api;
			
			// 检查是否有记住的用户名
			const savedUsername = uni.getStorageSync('savedUsername');
			if (savedUsername) {
				this.username = savedUsername;
			}
		},
		methods: {
			// 切换密码显示
			togglePassword() {
				this.showPassword = !this.showPassword;
			},

			// 登录核心方法（修复版）
			login() {
				// 防止重复点击
				if (this.loading) return;

				// 1. 验证账号密码非空
				if (!this.username.trim()) {
					uni.showToast({
						icon: "none",
						title: "请输入账号"
					});
					return;
				}

				if (!this.password.trim()) {
					uni.showToast({
						icon: "none",
						title: "请输入密码"
					});
					return;
				}

				// 2. 显示加载状态
				this.loading = true;

				// 3. 发起登录请求
				this.$api.post('/WxUser/login', {
					username: this.username,
					password: this.password
				}, {
					loading: true,
					loadingText: '登录中...',
					showError: true
				}).then(res => {
					this.loading = false;

					// 4. 登录成功处理
					if (res && res.code === 200) {
						// 保存token和用户信息
						const loginData = res.data || {};
						
						// 保存token（从LoginResponse中获取）
						if (loginData.token) {
							uni.setStorageSync('token', loginData.token);
						}
						
						// 保存用户信息
						const userInfo = loginData.user || {};
						uni.setStorageSync('userInfo', {
							userId: userInfo.userId,
							username: userInfo.username || this.username,
							userImg: userInfo.userImg,
							timestamp: new Date().getTime()
						});

						// 显示成功提示
						uni.showToast({
							icon: "success",
							title: res.message || "登录成功",
							duration: 1500
						});

						// 延迟后跳转到首页
						setTimeout(() => {
							this.navigateToHome();
						}, 1500);
					}
				}).catch(err => {
					this.loading = false;
					console.error('登录失败:', err);
				});
			},

			// 跳转到首页的方法（智能选择跳转方式）
			navigateToHome() {
				// 统一使用 reLaunch，避免非 tabBar 页面的 switchTab 报错
				this.fallbackNavigate();
			},

			// 备用跳转方案
			fallbackNavigate() {
				// 方法1：使用 reLaunch（关闭所有页面，打开首页）
				uni.reLaunch({
					url: '/pages/index/index',
					success: () => {
						console.log('跳转到首页成功 (reLaunch)');
					},
					fail: (err) => {
						console.error('reLaunch 失败:', err);

						// 方法2：如果 reLaunch 失败，尝试 redirectTo
						uni.redirectTo({
							url: '/pages/index/index',
							success: () => {
								console.log('跳转到首页成功 (redirectTo)');
							},
							fail: (err2) => {
								console.error('redirectTo 失败:', err2);

								// 方法3：最后尝试 navigateTo
								uni.navigateTo({
									url: '/pages/index/index',
									fail: (err3) => {
										console.error('所有跳转方式都失败:', err3);
										uni.showModal({
											title: '提示',
											content: '跳转失败，请检查页面路径是否正确',
											showCancel: false
										});
									}
								});
							}
						});
					}
				});
			},

			// 跳转到注册页面
			toRegister() {
				uni.navigateTo({
					url: '/pages/register/register'
				});
			},

			// 跳转到忘记密码页面
			toForgotPassword() {
				uni.showToast({
					icon: "none",
					title: "忘记密码功能开发中"
				});
				// 如果需要，可以跳转到忘记密码页面
				// uni.navigateTo({
				// 	url: '/pages/forgot/forgot'
				// });
			},

			// QQ登录
			qqLogin() {
				uni.showToast({
					icon: "none",
					title: "QQ登录功能开发中"
				});
			},

			// 微信登录
			wechatLogin() {
				uni.showToast({
					icon: "none",
					title: "微信登录功能开发中"
				});
			}
		}
	}
</script>

<style scoped>
	.content {
		height: 100vh;
		background: url("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202005%2F10%2F20200510005139_JR8fL.jpeg&refer=http%3A%2F%2Fc-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1714820289&t=e835cde99a094cbd98f9c318f25160ec") no-repeat;
		background-size: 100% 100%;
	}

	.loginBox {
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -60%);
		width: 90%;
		background-color: #fff;
		border-radius: 20rpx;
		padding: 60rpx;
		box-sizing: border-box;
	}

	h3 {
		color: rgb(247, 120, 172);
		font-size: 40rpx;
		letter-spacing: 10rpx;
		margin-bottom: 40rpx;
	}

	.ipt {
		height: 86rpx;
		display: flex;
		justify-content: flex-start;
		align-items: center;
		margin-bottom: 20rpx;
		background-color: #f5f5f5;
		border-radius: 10rpx;
		padding-left: 10rpx;
		padding-right: 10rpx;
	}

	.ipt input {
		flex: 1;
		margin-left: 20rpx;
		font-size: 28rpx;
		background-color: transparent;
	}

	.forgetPwd {
		font-size: 26rpx;
		color: #b5b5b5;
		text-align: end;
		padding: 0 10rpx;
		display: flex;
		justify-content: space-between;
		margin-top: 20rpx;
	}

	.forgetPwd span {
		padding: 10rpx;
	}

	.forgetPwd span:active {
		opacity: 0.7;
	}

	button {
		margin-top: 20rpx;
		line-height: 85rpx;
		text-align: center;
		background: linear-gradient(to right, rgb(255, 170, 127), rgb(247, 120, 172));
		border-radius: 40rpx;
		color: #fff;
		margin-top: 40rpx;
		font-size: 32rpx;
		border: none;
	}

	button[disabled] {
		opacity: 0.6;
	}

	.tip {
		text-align: center;
		font-size: 28rpx;
		position: fixed;
		bottom: 50rpx;
		left: 50%;
		transform: translate(-50%, -50%);
		color: #f4f4f4;
	}

	.tipbox {
		text-align: center;
		margin-top: 100rpx;
	}

	.otherUser {
		margin-top: 30rpx;
		display: flex;
		justify-content: center;
		gap: 60rpx;
	}

	.txt {
		font-size: 28rpx;
		color: #cbcbcb;
	}

	.otherUser .uni-icons {
		padding: 20rpx;
		border-radius: 50%;
		background-color: #f5f5f5;
	}

	.otherUser .uni-icons:active {
		opacity: 0.7;
	}
</style>